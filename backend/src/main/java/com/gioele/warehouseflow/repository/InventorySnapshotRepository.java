package com.gioele.warehouseflow.repository;

import com.gioele.warehouseflow.entity.InventorySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshot, Long> {
    List<InventorySnapshot> findAllByOrderByCreatedAtDesc();
}
