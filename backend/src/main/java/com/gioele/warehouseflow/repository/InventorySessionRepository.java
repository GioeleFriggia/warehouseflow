package com.gioele.warehouseflow.repository;

import com.gioele.warehouseflow.entity.InventorySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventorySessionRepository extends JpaRepository<InventorySession, Long> {
    List<InventorySession> findAllByOrderByCreatedAtDesc();
}
