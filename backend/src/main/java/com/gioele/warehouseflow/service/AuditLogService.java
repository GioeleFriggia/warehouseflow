package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.AuditLogResponse;
import com.gioele.warehouseflow.entity.AuditAction;
import com.gioele.warehouseflow.entity.AuditLog;
import com.gioele.warehouseflow.entity.User;
import com.gioele.warehouseflow.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(AuditAction action, String entityType, String entityId, User user, String oldValue, String newValue, String notes) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(user.getFirstName() + " " + user.getLastName());
        log.setRole(user.getRole().name());
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setNotes(notes);
        auditLogRepository.save(log);
    }

    public void logSystem(AuditAction action, String entityType, String entityId, String performedBy, String role, String oldValue, String newValue, String notes) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setRole(role);
        log.setOldValue(oldValue);
        log.setNewValue(newValue);
        log.setNotes(notes);
        auditLogRepository.save(log);
    }

    public List<AuditLogResponse> findAll(LocalDate dateFrom, LocalDate dateTo, String entityType, String performedBy) {
        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.plusDays(1).atStartOfDay() : null;

        return auditLogRepository.findAll().stream()
                .filter(log -> from == null || !log.getCreatedAt().isBefore(from))
                .filter(log -> to == null || log.getCreatedAt().isBefore(to))
                .filter(log -> !StringUtils.hasText(entityType) || entityType.equalsIgnoreCase(log.getEntityType()))
                .filter(log -> !StringUtils.hasText(performedBy) || log.getPerformedBy().toLowerCase().contains(performedBy.toLowerCase()))
                .sorted(Comparator.comparing(AuditLog::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public AuditLogResponse toResponse(AuditLog log) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(log.getId());
        response.setAction(log.getAction());
        response.setEntityType(log.getEntityType());
        response.setEntityId(log.getEntityId());
        response.setPerformedBy(log.getPerformedBy());
        response.setRole(log.getRole());
        response.setOldValue(log.getOldValue());
        response.setNewValue(log.getNewValue());
        response.setNotes(log.getNotes());
        response.setCreatedAt(log.getCreatedAt());
        return response;
    }
}
