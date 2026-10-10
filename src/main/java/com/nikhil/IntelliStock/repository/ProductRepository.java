package com.nikhil.IntelliStock.repository;

import com.nikhil.IntelliStock.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);

    @Query("SELECT p FROM Product p WHERE p.currentStock <= p.safetyStockLevel AND (p.category IS NULL OR LOWER(p.category) != 'end of life')")
    List<Product> findLowStockProducts();
}