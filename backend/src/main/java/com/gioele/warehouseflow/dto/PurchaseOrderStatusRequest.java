package com.gioele.warehouseflow.dto;

import com.gioele.warehouseflow.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class PurchaseOrderStatusRequest {
    @NotNull
    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
