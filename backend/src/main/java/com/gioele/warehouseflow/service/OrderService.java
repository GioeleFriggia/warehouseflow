package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.*;
import com.gioele.warehouseflow.entity.*;
import com.gioele.warehouseflow.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductService productService;
    private final UserService userService;

    public OrderService(PurchaseOrderRepository purchaseOrderRepository,
                        ProductService productService,
                        UserService userService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productService = productService;
        this.userService = userService;
    }

    public List<PurchaseOrderResponse> findAll() {
        return purchaseOrderRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public long countOpenOrders() {
        return purchaseOrderRepository.countByStatus(OrderStatus.DRAFT) + purchaseOrderRepository.countByStatus(OrderStatus.SUBMITTED);
    }

    @Transactional
    public PurchaseOrderResponse create(PurchaseOrderRequest request) {
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(request.getSupplier());
        order.setNotes(request.getNotes());
        order.setCreatedBy(userService.getCurrentUserEntity());

        List<PurchaseOrderItem> items = new ArrayList<>();
        for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(order);
            item.setProduct(productService.getEntity(itemRequest.getProductId()));
            item.setQuantity(itemRequest.getQuantity());
            items.add(item);
        }
        order.setItems(items);

        return toResponse(purchaseOrderRepository.save(order));
    }

    @Transactional
    public PurchaseOrderResponse updateStatus(Long id, PurchaseOrderStatusRequest request) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ordine non trovato"));
        order.setStatus(request.getStatus());
        return toResponse(purchaseOrderRepository.save(order));
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
