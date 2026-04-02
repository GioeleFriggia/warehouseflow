package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.AuditLogResponse;
import com.gioele.warehouseflow.service.AuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<AuditLogResponse> findAll(@RequestParam(required = false) LocalDate dateFrom,
                                          @RequestParam(required = false) LocalDate dateTo,
                                          @RequestParam(required = false) String entityType,
                                          @RequestParam(required = false) String performedBy) {
        return auditLogService.findAll(dateFrom, dateTo, entityType, performedBy);
    }
}
