package com.nikhil.IntelliStock.dto;

public class ProductResponseDTO {
    private Long id;
    private String sku;
    private String name;
    private String category;
    private Integer initialStock;
    private Integer currentStock;
    private Integer safetyStockLevel;
    private Boolean isReorderRecommended;

    public ProductResponseDTO() {}

    public ProductResponseDTO(Long id, String sku, String name, String category, Integer initialStock, Integer currentStock, Integer safetyStockLevel, Boolean isReorderRecommended) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.initialStock = initialStock;
        this.currentStock = currentStock;
        this.safetyStockLevel = safetyStockLevel;
        this.isReorderRecommended = isReorderRecommended;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getInitialStock() { return initialStock; }
    public void setInitialStock(Integer initialStock) { this.initialStock = initialStock; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getSafetyStockLevel() { return safetyStockLevel; }
    public void setSafetyStockLevel(Integer safetyStockLevel) { this.safetyStockLevel = safetyStockLevel; }

    public Boolean getIsReorderRecommended() { return isReorderRecommended; }
    public void setIsReorderRecommended(Boolean reorderRecommended) { isReorderRecommended = reorderRecommended; }
}