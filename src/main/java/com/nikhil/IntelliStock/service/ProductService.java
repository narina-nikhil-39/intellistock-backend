package com.nikhil.IntelliStock.service;

import com.nikhil.IntelliStock.dto.ProductRequestDTO;
import com.nikhil.IntelliStock.dto.ProductResponseDTO;
import com.nikhil.IntelliStock.entity.Product;
import com.nikhil.IntelliStock.exception.ResourceNotFoundException;
import com.nikhil.IntelliStock.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("Product with SKU " + request.getSku() + " already exists.");
        }

        Product product = new Product(
                null,
                request.getSku(),
                request.getName(),
                request.getCategory() != null ? request.getCategory() : "General",
                request.getInitialStock(),
                request.getInitialStock(),
                request.getSafetyStockLevel()
        );

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Admin sets product category to End of Life
    @Transactional
    public ProductResponseDTO setProductEndOfLife(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setCategory("End of Life");
        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public ProductResponseDTO mapToResponse(Product product) {
        boolean isEOL = "End of Life".equalsIgnoreCase(product.getCategory());
        boolean isReorderRecommended = !isEOL && product.getCurrentStock() <= product.getSafetyStockLevel();

        int recommendedQuantity = 0;
        if (isReorderRecommended) {
            int targetStock = product.getSafetyStockLevel() * 2;
            recommendedQuantity = Math.max(0, targetStock - product.getCurrentStock());
        }

        return new ProductResponseDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCategory(),
                product.getInitialStock(),
                product.getCurrentStock(),
                product.getSafetyStockLevel(),
                isReorderRecommended,
                recommendedQuantity
        );
    }
}