package com.gioele.warehouseflow.repository;

import com.gioele.warehouseflow.entity.InventorySessionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventorySessionItemRepository extends JpaRepository<InventorySessionItem, Long> {
    List<InventorySessionItem> findBySessionIdOrderByProduct_NameAsc(Long sessionId);
}
