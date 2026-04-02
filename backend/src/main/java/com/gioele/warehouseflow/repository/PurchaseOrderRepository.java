package com.gioele.warehouseflow.repository;

import com.gioele.warehouseflow.entity.OrderStatus;
import com.gioele.warehouseflow.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    long countByStatus(OrderStatus status);
    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();
}
