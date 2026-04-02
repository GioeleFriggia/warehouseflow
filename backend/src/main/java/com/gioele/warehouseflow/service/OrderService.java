package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.*;
import com.gioele.warehouseflow.entity.*;
import com.gioele.warehouseflow.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductService productService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public OrderService(PurchaseOrderRepository purchaseOrderRepository,
                        ProductService productService,
                        UserService userService,
                        AuditLogService auditLogService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productService = productService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    public List<PurchaseOrderResponse> findAll() {
        return purchaseOrderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public List<PurchaseOrderResponse> findFiltered(String supplier, OrderStatus status, LocalDate dateFrom, LocalDate dateTo) {
        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.plusDays(1).atStartOfDay() : null;

        return purchaseOrderRepository.findAll().stream()
                .filter(order -> !StringUtils.hasText(supplier)
                        || (order.getSupplier() != null && order.getSupplier().toLowerCase().contains(supplier.toLowerCase())))
                .filter(order -> status == null || order.getStatus() == status)
                .filter(order -> from == null || !order.getCreatedAt().isBefore(from))
                .filter(order -> to == null || order.getCreatedAt().isBefore(to))
                .sorted(Comparator.comparing(PurchaseOrder::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public long countOpenOrders() {
        return purchaseOrderRepository.countByStatus(OrderStatus.DRAFT) + purchaseOrderRepository.countByStatus(OrderStatus.SUBMITTED);
    }

    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(request.getSupplier());
        order.setNotes(request.getNotes());
        order.setCreatedBy(currentUser);

        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(order);
            item.setProduct(productService.getEntity(itemRequest.getProductId()));
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        order.setItems(items);

        PurchaseOrder saved = purchaseOrderRepository.save(order);
        auditLogService.log(AuditAction.ORDER_CREATED, "PurchaseOrder", String.valueOf(saved.getId()), currentUser,
                null, "supplier=" + saved.getSupplier() + ",status=" + saved.getStatus(), "Creazione ordine");
        return toResponse(saved);
    }

    @Transactional
    public PurchaseOrderResponse updateStatus(Long id, PurchaseOrderStatusRequest request) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ordine non trovato"));
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(request.getStatus());
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        auditLogService.log(AuditAction.ORDER_STATUS_UPDATED, "PurchaseOrder", String.valueOf(saved.getId()),
                userService.getCurrentUserEntity(), String.valueOf(oldStatus), String.valueOf(saved.getStatus()), "Cambio stato ordine");
        return toResponse(saved);
    }

    public PurchaseOrderResponse toResponse(PurchaseOrder order) {
        PurchaseOrderResponse response = new PurchaseOrderResponse();
        response.setId(order.getId());
        response.setSupplier(order.getSupplier());
        response.setStatus(order.getStatus());
        response.setNotes(order.getNotes());
        response.setCreatedBy(order.getCreatedBy().getFirstName() + " " + order.getCreatedBy().getLastName());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setItems(order.getItems().stream().map(item -> {
            PurchaseOrderItemResponse itemResponse = new PurchaseOrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProduct().getId());
            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setSku(item.getProduct().getSku());
            itemResponse.setQuantity(item.getQuantity());
            return itemResponse;
        }).toList());
        return response;
    }
}
