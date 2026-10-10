package com.nikhil.IntelliStock.service;

import com.nikhil.IntelliStock.dto.DemandForecastDTO;
import com.nikhil.IntelliStock.entity.Product;
import com.nikhil.IntelliStock.entity.StockTransaction;
import com.nikhil.IntelliStock.entity.TransactionType;
import com.nikhil.IntelliStock.exception.ResourceNotFoundException;
import com.nikhil.IntelliStock.repository.ProductRepository;
import com.nikhil.IntelliStock.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForecastService {

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;

    public ForecastService(ProductRepository productRepository, TransactionRepository transactionRepository) {
        this.productRepository = productRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public DemandForecastDTO calculateForecast(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        List<StockTransaction> transactions = transactionRepository.findByProductId(productId);

        List<StockTransaction> outwardList = transactions.stream()
                .filter(t -> t.getType() == TransactionType.OUTWARD)
                .toList();

        int totalDispatched = outwardList.stream().mapToInt(StockTransaction::getQuantity).sum();

        double dailyBurnRate;
        if (outwardList.isEmpty()) {
            dailyBurnRate = 0.0;
        } else {
            LocalDate earliest = outwardList.stream()
                    .map(t -> t.getCreatedAt().toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(LocalDate.now());

            long daysBetween = ChronoUnit.DAYS.between(earliest, LocalDate.now());
            long windowDays = Math.max(1, daysBetween + 1);
            dailyBurnRate = Math.round(((double) totalDispatched / windowDays) * 100.0) / 100.0;
        }

        Integer estimatedDaysRemaining = null;
        LocalDate projectedStockoutDate = null;
        String urgency = "STABLE";

        if (dailyBurnRate > 0) {
            estimatedDaysRemaining = (int) Math.floor(product.getCurrentStock() / dailyBurnRate);
            projectedStockoutDate = LocalDate.now().plusDays(estimatedDaysRemaining);

            if (estimatedDaysRemaining <= 3) {
                urgency = "CRITICAL";
            } else if (estimatedDaysRemaining <= 7) {
                urgency = "WARNING";
            }
        } else if (product.getCurrentStock() <= product.getSafetyStockLevel()) {
            urgency = "WARNING";
        }

        int targetFourteenDayDemand = (int) Math.ceil(dailyBurnRate * 14);
        int recommendedRestock = Math.max(
                0,
                (targetFourteenDayDemand + product.getSafetyStockLevel()) - product.getCurrentStock()
        );

        return new DemandForecastDTO(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getCurrentStock(),
                dailyBurnRate,
                estimatedDaysRemaining,
                projectedStockoutDate,
                urgency,
                recommendedRestock
        );
    }

    @Transactional(readOnly = true)
    public List<DemandForecastDTO> getAllForecasts() {
        List<Product> products = productRepository.findAll();
        List<DemandForecastDTO> forecasts = new ArrayList<>();

        for (Product product : products) {
            if (!"End of Life".equalsIgnoreCase(product.getCategory())) {
                forecasts.add(calculateForecast(product.getId()));
            }
        }

        return forecasts;
    }
}
