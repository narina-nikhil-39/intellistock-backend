package com.nikhil.IntelliStock.repository;

import com.nikhil.IntelliStock.entity.StockTransaction;
import com.nikhil.IntelliStock.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByProductId(Long productId);
    List<StockTransaction> findByProductIdAndTypeAndCreatedAtAfter(
            Long productId, TransactionType type, LocalDateTime afterDate);
}