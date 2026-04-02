package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.InventoryCountRequest;
import com.gioele.warehouseflow.dto.InventorySessionCreateRequest;
import com.gioele.warehouseflow.dto.InventorySessionItemResponse;
import com.gioele.warehouseflow.dto.InventorySessionResponse;
import com.gioele.warehouseflow.entity.*;
import com.gioele.warehouseflow.repository.InventorySessionItemRepository;
import com.gioele.warehouseflow.repository.InventorySessionRepository;
import com.gioele.warehouseflow.repository.ProductRepository;
import com.gioele.warehouseflow.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class InventoryService {

    private final InventorySessionRepository inventorySessionRepository;
    private final InventorySessionItemRepository inventorySessionItemRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final UserService userService;

    public InventoryService(InventorySessionRepository inventorySessionRepository,
                            InventorySessionItemRepository inventorySessionItemRepository,
                            ProductRepository productRepository,
                            StockMovementRepository stockMovementRepository,
                            UserService userService) {
        this.inventorySessionRepository = inventorySessionRepository;
        this.inventorySessionItemRepository = inventorySessionItemRepository;
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.userService = userService;
    }

    @Transactional
    public InventorySessionResponse create(InventorySessionCreateRequest request) {
        InventorySession session = new InventorySession();
        session.setName(request.getName());
        session.setCategoryFilter(request.getCategory());
        session.setSupplierFilter(request.getSupplier());
        session.setLocationFilter(request.getLocation());
        session.setNotes(request.getNotes());
        session.setApplyAdjustmentsOnComplete(request.isApplyAdjustmentsOnComplete());
        session.setCreatedBy(userService.getCurrentUserEntity());

        List<Product> products = productRepository.findAll().stream()
                .filter(product -> !StringUtils.hasText(request.getCategory()) || request.getCategory().equalsIgnoreCase(product.getCategory()))
                .filter(product -> !StringUtils.hasText(request.getSupplier()) || request.getSupplier().equalsIgnoreCase(product.getSupplier()))
                .filter(product -> !StringUtils.hasText(request.getLocation()) || request.getLocation().equalsIgnoreCase(product.getWarehouseLocation()))
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .toList();

        if (products.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "Nessun prodotto trovato con i filtri scelti");
        }

        List<InventorySessionItem> items = new ArrayList<>();
        for (Product product : products) {
            InventorySessionItem item = new InventorySessionItem();
            item.setSession(session);
            item.setProduct(product);
            item.setSystemQuantity(product.getQuantityAvailable());
            items.add(item);
        }
        session.setItems(items);

        return toResponse(inventorySessionRepository.save(session), true);
    }

    public List<InventorySessionResponse> findAll() {
        return inventorySessionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(session -> toResponse(session, false))
                .toList();
    }

    public InventorySessionResponse findById(Long id) {
        InventorySession session = inventorySessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sessione inventario non trovata"));
        return toResponse(session, true);
    }

    @Transactional
    public InventorySessionResponse count(InventoryCountRequest request) {
        InventorySessionItem item = inventorySessionItemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Riga inventario non trovata"));

        if (item.getSession().getStatus() == InventorySessionStatus.COMPLETED) {
            throw new ResponseStatusException(BAD_REQUEST, "Inventario già chiuso");
        }

        item.setCountedQuantity(request.getCountedQuantity());
        item.setNotes(request.getNotes());
        item.setCountedAt(LocalDateTime.now());
        item.setCountedBy(userService.getCurrentUserEntity());

        inventorySessionItemRepository.save(item);
        return findById(item.getSession().getId());
    }

    @Transactional
    public InventorySessionResponse complete(Long id) {
        InventorySession session = inventorySessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sessione inventario non trovata"));

        if (session.getStatus() == InventorySessionStatus.COMPLETED) {
            return toResponse(session, true);
        }

        User currentUser = userService.getCurrentUserEntity();

        if (session.isApplyAdjustmentsOnComplete()) {
            for (InventorySessionItem item : session.getItems()) {
                if (item.getCountedQuantity() == null) {
                    continue;
                }

                int systemQty = item.getProduct().getQuantityAvailable();
                int countedQty = item.getCountedQuantity();

                if (systemQty != countedQty) {
                    Product product = item.getProduct();
                    product.setQuantityAvailable(countedQty);
                    productRepository.save(product);

                    StockMovement movement = new StockMovement();
                    movement.setProduct(product);
                    movement.setPerformedBy(currentUser);
                    movement.setMovementType(MovementType.ADJUSTMENT);
                    movement.setQuantity(countedQty);
                    movement.setSourceLocation(product.getWarehouseLocation());
                    movement.setDestinationLocation(product.getWarehouseLocation());
                    movement.setNotes("Chiusura inventario #" + session.getId() + " - " + (item.getNotes() == null ? "" : item.getNotes()));
                    stockMovementRepository.save(movement);
                }
            }
        }

        session.setStatus(InventorySessionStatus.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());

        return toResponse(inventorySessionRepository.save(session), true);
    }

    private InventorySessionResponse toResponse(InventorySession session, boolean includeItems) {
        InventorySessionResponse response = new InventorySessionResponse();
        response.setId(session.getId());
        response.setName(session.getName());
        response.setCategoryFilter(session.getCategoryFilter());
        response.setSupplierFilter(session.getSupplierFilter());
        response.setLocationFilter(session.getLocationFilter());
        response.setNotes(session.getNotes());
        response.setStatus(session.getStatus());
        response.setApplyAdjustmentsOnComplete(session.isApplyAdjustmentsOnComplete());
        response.setCreatedBy(session.getCreatedBy().getFirstName() + " " + session.getCreatedBy().getLastName());
        response.setCreatedAt(session.getCreatedAt());
        response.setCompletedAt(session.getCompletedAt());

        List<InventorySessionItem> items = session.getItems();
        response.setTotalItems(items.size());
        response.setCountedItems(items.stream().filter(item -> item.getCountedQuantity() != null).count());

        if (includeItems) {
            response.setItems(items.stream().map(this::toItemResponse).toList());
        }

        return response;
    }

    private InventorySessionItemResponse toItemResponse(InventorySessionItem item) {
        InventorySessionItemResponse response = new InventorySessionItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct().getId());
        response.setSku(item.getProduct().getSku());
        response.setProductName(item.getProduct().getName());
        response.setCategory(item.getProduct().getCategory());
        response.setSupplier(item.getProduct().getSupplier());
        response.setWarehouseLocation(item.getProduct().getWarehouseLocation());
        response.setSystemQuantity(item.getSystemQuantity());
        response.setCountedQuantity(item.getCountedQuantity());
        if (item.getCountedQuantity() != null) {
            response.setDifference(item.getCountedQuantity() - item.getSystemQuantity());
        }
        response.setNotes(item.getNotes());
        response.setCountedAt(item.getCountedAt());
        if (item.getCountedBy() != null) {
            response.setCountedBy(item.getCountedBy().getFirstName() + " " + item.getCountedBy().getLastName());
        }
        return response;
    }
}
