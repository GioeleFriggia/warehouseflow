package com.gioele.warehouseflow.repository;

import com.gioele.warehouseflow.entity.PurchaseOrderDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderDocumentRepository extends JpaRepository<PurchaseOrderDocument, Long> {
    List<PurchaseOrderDocument> findByPurchaseOrderIdOrderByUploadedAtDesc(Long purchaseOrderId);
}
