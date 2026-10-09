package com.nikhil.IntelliStock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String category;

    @Column(nullable = false)
    private Integer initialStock;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer safetyStockLevel;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<StockTransaction> transactions = new ArrayList<>();

    public Product() {}

    public Product(Long id, String sku, String name, String category, Integer initialStock, Integer currentStock, Integer safetyStockLevel) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
        this.initialStock = initialStock;
        this.currentStock = currentStock;
        this.safetyStockLevel = safetyStockLevel;
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

    public List<StockTransaction> getTransactions() { return transactions; }
    public void setTransactions(List<StockTransaction> transactions) { this.transactions = transactions; }
}