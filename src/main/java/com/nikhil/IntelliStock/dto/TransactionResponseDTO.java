package com.nikhil.IntelliStock.dto;

import com.nikhil.IntelliStock.entity.TransactionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionResponseDTO {
    private Long id;
    private Long productId;
    private String productSku;
    private String productName;
    private TransactionType type;
    private Integer quantity;
    private LocalDate billDate;
    private LocalDate deliveryDate;
    private Boolean isAnomaly;
    private LocalDateTime createdAt;
    private String performedByUsername;
    private String performedByRole;

    public TransactionResponseDTO() {}

    public TransactionResponseDTO(Long id, Long productId, String productSku, String productName, TransactionType type, Integer quantity, LocalDate billDate, LocalDate deliveryDate, Boolean isAnomaly, LocalDateTime createdAt, String performedByUsername, String performedByRole) {
        this.id = id;
        this.productId = productId;
        this.productSku = productSku;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.billDate = billDate;
        this.deliveryDate = deliveryDate;
        this.isAnomaly = isAnomaly;
        this.createdAt = createdAt;
        this.performedByUsername = performedByUsername;
        this.performedByRole = performedByRole;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(LocalDate deliveryDate) { this.deliveryDate = deliveryDate; }

    public Boolean getIsAnomaly() { return isAnomaly; }
    public void setIsAnomaly(Boolean anomaly) { isAnomaly = anomaly; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPerformedByUsername() { return performedByUsername; }
    public void setPerformedByUsername(String performedByUsername) { this.performedByUsername = performedByUsername; }

    public String getPerformedByRole() { return performedByRole; }
    public void setPerformedByRole(String performedByRole) { this.performedByRole = performedByRole; }
}