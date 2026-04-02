package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.InventoryCountRequest;
import com.gioele.warehouseflow.dto.InventorySessionCreateRequest;
import com.gioele.warehouseflow.dto.InventorySessionResponse;
import com.gioele.warehouseflow.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public List<InventorySessionResponse> findAll() {
        return inventoryService.findAll();
    }

    @GetMapping("/sessions/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public InventorySessionResponse findById(@PathVariable Long id) {
        return inventoryService.findById(id);
    }

    @PostMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InventorySessionResponse create(@Valid @RequestBody InventorySessionCreateRequest request) {
        return inventoryService.create(request);
    }

    @PostMapping("/counts")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public InventorySessionResponse count(@Valid @RequestBody InventoryCountRequest request) {
        return inventoryService.count(request);
    }

    @PostMapping("/sessions/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public InventorySessionResponse complete(@PathVariable Long id) {
        return inventoryService.complete(id);
    }
}
