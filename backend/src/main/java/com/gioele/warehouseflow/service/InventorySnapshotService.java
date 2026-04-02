package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.InventorySnapshotCreateRequest;
import com.gioele.warehouseflow.dto.InventorySnapshotItemResponse;
import com.gioele.warehouseflow.dto.InventorySnapshotResponse;
import com.gioele.warehouseflow.entity.*;
import com.gioele.warehouseflow.repository.InventorySnapshotRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventorySnapshotService {

    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final ProductService productService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public InventorySnapshotService(InventorySnapshotRepository inventorySnapshotRepository,
                                    ProductService productService,
                                    UserService userService,
                                    AuditLogService auditLogService) {
        this.inventorySnapshotRepository = inventorySnapshotRepository;
        this.productService = productService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    public List<InventorySnapshotResponse> findAll() {
        return inventorySnapshotRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    @Transactional
    public InventorySnapshotResponse create(InventorySnapshotCreateRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        InventorySnapshot snapshot = new InventorySnapshot();
        snapshot.setName(request.getName() == null || request.getName().isBlank()
                ? "Snapshot " + LocalDateTime.now()
                : request.getName());
        snapshot.setNotes(request.getNotes());
        snapshot.setCreatedBy(currentUser);

        List<InventorySnapshotItem> items = new ArrayList<>();
        for (Product product : productService.findEntities(null, null, null, false)) {
            InventorySnapshotItem item = new InventorySnapshotItem();
            item.setSnapshot(snapshot);
            item.setProduct(product);
            item.setQuantityAvailable(product.getQuantityAvailable());
            item.setMinimumThreshold(product.getMinimumThreshold());
            item.setCategory(product.getCategory());
            item.setSupplier(product.getSupplier());
            items.add(item);
        }
        snapshot.setItems(items);

        InventorySnapshot saved = inventorySnapshotRepository.save(snapshot);
        auditLogService.log(AuditAction.INVENTORY_SNAPSHOT_CREATED, "InventorySnapshot", String.valueOf(saved.getId()), currentUser,
                null, "items=" + saved.getItems().size(), saved.getName());
        return toResponse(saved);
    }

    public InventorySnapshotResponse toResponse(InventorySnapshot snapshot) {
        InventorySnapshotResponse response = new InventorySnapshotResponse();
        response.setId(snapshot.getId());
        response.setName(snapshot.getName());
        response.setNotes(snapshot.getNotes());
        response.setCreatedBy(snapshot.getCreatedBy().getFirstName() + " " + snapshot.getCreatedBy().getLastName());
        response.setCreatedAt(snapshot.getCreatedAt());
        response.setItems(snapshot.getItems().stream().map(item -> {
            InventorySnapshotItemResponse itemResponse = new InventorySnapshotItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProduct().getId());
            itemResponse.setSku(item.getProduct().getSku());
            itemResponse.setProductName(item.getProduct().getName());
            itemResponse.setCategory(item.getCategory());
            itemResponse.setSupplier(item.getSupplier());
            itemResponse.setQuantityAvailable(item.getQuantityAvailable());
            itemResponse.setMinimumThreshold(item.getMinimumThreshold());
            itemResponse.setLowStock(item.getQuantityAvailable() <= item.getMinimumThreshold());
            return itemResponse;
        }).toList());
        return response;
    }
}
