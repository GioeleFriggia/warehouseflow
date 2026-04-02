package com.gioele.warehouseflow.repository;

import com.gioele.warehouseflow.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
    List<StockMovement> findTop50ByOrderByCreatedAtDesc();
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
