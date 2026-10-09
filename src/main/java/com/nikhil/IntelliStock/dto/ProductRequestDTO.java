package com.nikhil.IntelliStock.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRequestDTO {

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    private String name;

    private String category;

    @NotNull(message = "Initial stock cannot be null")
    @Min(value = 0, message = "Initial stock cannot be negative")
    private Integer initialStock;

    @NotNull(message = "Safety stock level cannot be null")
    @Min(value = 0, message = "Safety stock level cannot be negative")
    private Integer safetyStockLevel;

    public ProductRequestDTO() {}

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getInitialStock() { return initialStock; }
    public void setInitialStock(Integer initialStock) { this.initialStock = initialStock; }

    public Integer getSafetyStockLevel() { return safetyStockLevel; }
    public void setSafetyStockLevel(Integer safetyStockLevel) { this.safetyStockLevel = safetyStockLevel; }
}