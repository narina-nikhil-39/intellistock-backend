package com.nikhil.IntelliStock.service;

import com.nikhil.IntelliStock.dto.DemandForecastDTO;
import com.nikhil.IntelliStock.entity.Product;
import com.nikhil.IntelliStock.entity.Role;
import com.nikhil.IntelliStock.entity.StockTransaction;
import com.nikhil.IntelliStock.entity.TransactionType;
import com.nikhil.IntelliStock.entity.User;
import com.nikhil.IntelliStock.repository.ProductRepository;
import com.nikhil.IntelliStock.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ForecastService forecastService;

    private Product testProduct;
    private User testUser;

    @BeforeEach
    void setUp() {
        testProduct = new Product("TEST-SKU", "Widget A", "Electronics", 20, 10);
        testProduct.setId(1L);

        testUser = new User(1L, "admin", "hashed_pwd", Role.ROLE_ADMIN);
    }

    @Test
    @DisplayName("Should return stable forecast when there are zero outward dispatches")
    void calculateForecast_ZeroOutwardTransactions_ReturnsStable() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(transactionRepository.findByProductId(1L)).thenReturn(Collections.emptyList());

        DemandForecastDTO forecast = forecastService.calculateForecast(1L);

        assertNotNull(forecast);
        assertEquals(0.0, forecast.getDailyBurnRate());
        assertNull(forecast.getEstimatedDaysRemaining());
        assertEquals("STABLE", forecast.getRestockUrgency());
        assertEquals(0, forecast.getRecommendedRestockQuantity());
    }

    @Test
    @DisplayName("Should flag CRITICAL when estimated stockout is within 3 days")
    void calculateForecast_HighVelocityOutward_FlagsCriticalUrgency() {
        StockTransaction outwardTx = new StockTransaction(
                testProduct,
                testUser,
                TransactionType.OUTWARD,
                20,
                LocalDate.now(),
                LocalDate.now(),
                false
        );
        outwardTx.setCreatedAt(LocalDateTime.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(transactionRepository.findByProductId(1L)).thenReturn(List.of(outwardTx));

        DemandForecastDTO forecast = forecastService.calculateForecast(1L);

        assertNotNull(forecast);
        assertTrue(forecast.getDailyBurnRate() > 0);
        assertNotNull(forecast.getEstimatedDaysRemaining());
        assertTrue(forecast.getEstimatedDaysRemaining() <= 3);
        assertEquals("CRITICAL", forecast.getRestockUrgency());
        assertEquals(20.0, forecast.getDailyBurnRate());
        assertEquals(1, forecast.getEstimatedDaysRemaining());
        assertEquals(270, forecast.getRecommendedRestockQuantity());
    }

    @Test
    @DisplayName("Should flag WARNING at seven days and exclude inward transactions from burn rate")
    void calculateForecast_SevenDaysOfStock_FlagsWarningAndUsesOutwardTransactionsOnly() {
        testProduct.setCurrentStock(70);
        StockTransaction outwardTx = new StockTransaction(
                testProduct,
                testUser,
                TransactionType.OUTWARD,
                10,
                LocalDate.now(),
                LocalDate.now(),
                false
        );
        outwardTx.setCreatedAt(LocalDateTime.now());
        StockTransaction inwardTx = new StockTransaction(
                testProduct,
                testUser,
                TransactionType.INWARD,
                100,
                LocalDate.now(),
                LocalDate.now(),
                false
        );
        inwardTx.setCreatedAt(LocalDateTime.now());

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(transactionRepository.findByProductId(1L)).thenReturn(List.of(outwardTx, inwardTx));

        DemandForecastDTO forecast = forecastService.calculateForecast(1L);

        assertEquals(10.0, forecast.getDailyBurnRate());
        assertEquals(7, forecast.getEstimatedDaysRemaining());
        assertEquals("WARNING", forecast.getRestockUrgency());
        assertEquals(80, forecast.getRecommendedRestockQuantity());
    }
}
