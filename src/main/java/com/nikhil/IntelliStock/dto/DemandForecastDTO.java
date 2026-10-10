package com.nikhil.IntelliStock.dto;

import java.time.LocalDate;

public class DemandForecastDTO {

    private Long productId;
    private String sku;
    private String productName;
    private Integer currentStock;
    private Double dailyBurnRate;
    private Integer estimatedDaysRemaining;
    private LocalDate projectedStockoutDate;
    private String restockUrgency; // CRITICAL, WARNING, STABLE
    private Integer recommendedRestockQuantity;

    public DemandForecastDTO() {}

    public DemandForecastDTO(Long productId, String sku, String productName, Integer currentStock,
                             Double dailyBurnRate, Integer estimatedDaysRemaining,
                             LocalDate projectedStockoutDate, String restockUrgency,
                             Integer recommendedRestockQuantity) {
        this.productId = productId;
        this.sku = sku;
        this.productName = productName;
        this.currentStock = currentStock;
        this.dailyBurnRate = dailyBurnRate;
        this.estimatedDaysRemaining = estimatedDaysRemaining;
        this.projectedStockoutDate = projectedStockoutDate;
        this.restockUrgency = restockUrgency;
        this.recommendedRestockQuantity = recommendedRestockQuantity;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Double getDailyBurnRate() { return dailyBurnRate; }
    public void setDailyBurnRate(Double dailyBurnRate) { this.dailyBurnRate = dailyBurnRate; }

    public Integer getEstimatedDaysRemaining() { return estimatedDaysRemaining; }
    public void setEstimatedDaysRemaining(Integer estimatedDaysRemaining) { this.estimatedDaysRemaining = estimatedDaysRemaining; }

    public LocalDate getProjectedStockoutDate() { return projectedStockoutDate; }
    public void setProjectedStockoutDate(LocalDate projectedStockoutDate) { this.projectedStockoutDate = projectedStockoutDate; }

    public String getRestockUrgency() { return restockUrgency; }
    public void setRestockUrgency(String restockUrgency) { this.restockUrgency = restockUrgency; }

    public Integer getRecommendedRestockQuantity() { return recommendedRestockQuantity; }
    public void setRecommendedRestockQuantity(Integer recommendedRestockQuantity) { this.recommendedRestockQuantity = recommendedRestockQuantity; }
}
