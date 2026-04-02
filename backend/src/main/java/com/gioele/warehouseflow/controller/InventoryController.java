package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.InventorySnapshotCreateRequest;
import com.gioele.warehouseflow.dto.InventorySnapshotResponse;
import com.gioele.warehouseflow.service.InventorySnapshotService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class InventoryController {

    private final InventorySnapshotService inventorySnapshotService;

    public InventoryController(InventorySnapshotService inventorySnapshotService) {
        this.inventorySnapshotService = inventorySnapshotService;
    }

    @GetMapping("/snapshots")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public List<InventorySnapshotResponse> findAll() {
        return inventorySnapshotService.findAll();
    }

    @PostMapping("/snapshots")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public InventorySnapshotResponse create(@RequestBody InventorySnapshotCreateRequest request) {
        return inventorySnapshotService.create(request);
    }
}
