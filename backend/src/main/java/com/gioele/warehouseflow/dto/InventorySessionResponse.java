package com.gioele.warehouseflow.dto;

import com.gioele.warehouseflow.entity.InventorySessionStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventorySessionResponse {

    private Long id;
    private String name;
    private String categoryFilter;
    private String supplierFilter;
    private String locationFilter;
    private String notes;
    private InventorySessionStatus status;
    private boolean applyAdjustmentsOnComplete;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private int totalItems;
    private long countedItems;
    private List<InventorySessionItemResponse> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategoryFilter() { return categoryFilter; }
    public void setCategoryFilter(String categoryFilter) { this.categoryFilter = categoryFilter; }
    public String getSupplierFilter() { return supplierFilter; }
    public void setSupplierFilter(String supplierFilter) { this.supplierFilter = supplierFilter; }
    public String getLocationFilter() { return locationFilter; }
    public void setLocationFilter(String locationFilter) { this.locationFilter = locationFilter; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public InventorySessionStatus getStatus() { return status; }
    public void setStatus(InventorySessionStatus status) { this.status = status; }
    public boolean isApplyAdjustmentsOnComplete() { return applyAdjustmentsOnComplete; }
    public void setApplyAdjustmentsOnComplete(boolean applyAdjustmentsOnComplete) { this.applyAdjustmentsOnComplete = applyAdjustmentsOnComplete; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    public long getCountedItems() { return countedItems; }
    public void setCountedItems(long countedItems) { this.countedItems = countedItems; }
    public List<InventorySessionItemResponse> getItems() { return items; }
    public void setItems(List<InventorySessionItemResponse> items) { this.items = items; }
}
