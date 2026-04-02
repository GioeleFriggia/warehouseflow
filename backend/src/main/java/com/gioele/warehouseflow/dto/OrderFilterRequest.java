package com.gioele.warehouseflow.dto;

import com.gioele.warehouseflow.entity.OrderStatus;

import java.time.LocalDate;

public class OrderFilterRequest {
    private String supplier;
    private OrderStatus status;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }

    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }
}
