package com.gioele.warehouseflow.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InventorySnapshotResponse {
    private Long id;
    private String name;
    private String notes;
    private String createdBy;
    private LocalDateTime createdAt;
    private List<InventorySnapshotItemResponse> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<InventorySnapshotItemResponse> getItems() { return items; }
    public void setItems(List<InventorySnapshotItemResponse> items) { this.items = items; }
}
