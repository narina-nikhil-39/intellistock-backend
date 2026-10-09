package com.nikhil.IntelliStock.service;

import com.nikhil.IntelliStock.dto.TransactionRequestDTO;
import com.nikhil.IntelliStock.dto.TransactionResponseDTO;
import com.nikhil.IntelliStock.entity.Product;
import com.nikhil.IntelliStock.entity.StockTransaction;
import com.nikhil.IntelliStock.entity.TransactionType;
import com.nikhil.IntelliStock.exception.InsufficientStockException;
import com.nikhil.IntelliStock.exception.ResourceNotFoundException;
import com.nikhil.IntelliStock.repository.ProductRepository;
import com.nikhil.IntelliStock.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    public TransactionService(TransactionRepository transactionRepository, ProductRepository productRepository) {
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public TransactionResponseDTO recordTransaction(TransactionRequestDTO request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if ("End of Life".equalsIgnoreCase(product.getCategory()) && request.getType() == TransactionType.OUTWARD) {
            throw new IllegalArgumentException("Cannot dispatch an End of Life product.");
        }

        boolean isAnomaly = detectQuantityAnomaly(product.getId(), request.getQuantity());

        if (request.getType() == TransactionType.INWARD) {
            product.setCurrentStock(product.getCurrentStock() + request.getQuantity());
        } else if (request.getType() == TransactionType.OUTWARD) {
            if (product.getCurrentStock() < request.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock. Available: " 
                        + product.getCurrentStock() + ", requested: " + request.getQuantity());
            }
            product.setCurrentStock(product.getCurrentStock() - request.getQuantity());
        }

        productRepository.save(product);

        StockTransaction transaction = new StockTransaction(
                product,
                request.getType(),
                request.getQuantity(),
                request.getBillDate(),
                request.getDeliveryDate(),
                isAnomaly
        );

        StockTransaction savedTx = transactionRepository.save(transaction);
        return mapToResponse(savedTx);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByProductId(Long productId) {
        return transactionRepository.findByProductId(productId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private boolean detectQuantityAnomaly(Long productId, Integer incomingQuantity) {
        List<StockTransaction> previousTransactions = transactionRepository.findByProductId(productId);
        if (previousTransactions.size() < 3) {
            return false;
        }

        double average = previousTransactions.stream()
                .mapToInt(StockTransaction::getQuantity)
                .average()
                .orElse(0.0);

        return incomingQuantity > (average * 3.0);
    }

    private TransactionResponseDTO mapToResponse(StockTransaction tx) {
        return new TransactionResponseDTO(
                tx.getId(),
                tx.getProduct().getId(),
                tx.getProduct().getSku(),
                tx.getProduct().getName(),
                tx.getType(),
                tx.getQuantity(),
                tx.getBillDate(),
                tx.getDeliveryDate(),
                tx.getIsAnomaly(),
                tx.getCreatedAt()
        );
    }
}