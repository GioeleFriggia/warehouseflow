package com.gioele.warehouseflow.repository;

import com.gioele.warehouseflow.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
