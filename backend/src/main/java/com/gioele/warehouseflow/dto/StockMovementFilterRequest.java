package com.gioele.warehouseflow.dto;

import com.gioele.warehouseflow.entity.MovementType;

import java.time.LocalDate;

public class StockMovementFilterRequest {
    private Long productId;
    private MovementType movementType;
    private String performedBy;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }

    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
}
