package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.OrderReceiveRequest;
import com.gioele.warehouseflow.dto.PurchaseOrderItemRequest;
import com.gioele.warehouseflow.dto.PurchaseOrderItemResponse;
import com.gioele.warehouseflow.dto.PurchaseOrderRequest;
import com.gioele.warehouseflow.dto.PurchaseOrderResponse;
import com.gioele.warehouseflow.dto.PurchaseOrderStatusRequest;
import com.gioele.warehouseflow.entity.OrderStatus;
import com.gioele.warehouseflow.entity.Product;
import com.gioele.warehouseflow.entity.PurchaseOrder;
import com.gioele.warehouseflow.entity.PurchaseOrderItem;
import com.gioele.warehouseflow.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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
        return purchaseOrderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<PurchaseOrderResponse> findFiltered(String supplier,
                                                    OrderStatus status,
                                                    LocalDate from,
                                                    LocalDate to) {
        return purchaseOrderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .filter(order -> {
                    if (!StringUtils.hasText(supplier)) {
                        return true;
                    }
                    return order.getSupplier() != null
                            && order.getSupplier().toLowerCase().contains(supplier.toLowerCase());
                })
                .filter(order -> status == null || order.getStatus() == status)
                .filter(order -> from == null || !order.getCreatedAt().toLocalDate().isBefore(from))
                .filter(order -> to == null || !order.getCreatedAt().toLocalDate().isAfter(to))
                .map(this::toResponse)
                .toList();
    }

    public long countOpenOrders() {
        return purchaseOrderRepository.countByStatus(OrderStatus.DRAFT)
                + purchaseOrderRepository.countByStatus(OrderStatus.SUBMITTED);
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

            Product product = productService.getEntity(itemRequest.getProductId());
            item.setProduct(product);
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

    @Transactional
    public PurchaseOrderResponse receiveOrder(Long id, OrderReceiveRequest request) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ordine non trovato"));

        order.setStatus(OrderStatus.RECEIVED);

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