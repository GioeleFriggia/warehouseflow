package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.PurchaseOrderRequest;
import com.gioele.warehouseflow.dto.PurchaseOrderResponse;
import com.gioele.warehouseflow.dto.PurchaseOrderStatusRequest;
import com.gioele.warehouseflow.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<PurchaseOrderResponse> findAll() {
        return orderService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PurchaseOrderResponse create(@Valid @RequestBody PurchaseOrderRequest request) {
        return orderService.create(request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PurchaseOrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody PurchaseOrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }
}
