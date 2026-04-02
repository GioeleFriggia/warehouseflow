package com.gioele.warehouseflow.dto;

import jakarta.validation.constraints.NotBlank;

public class InventorySessionCreateRequest {

    @NotBlank
    private String name;
    private String category;
    private String supplier;
    private String location;
    private String notes;
    private boolean applyAdjustmentsOnComplete = true;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isApplyAdjustmentsOnComplete() { return applyAdjustmentsOnComplete; }
    public void setApplyAdjustmentsOnComplete(boolean applyAdjustmentsOnComplete) { this.applyAdjustmentsOnComplete = applyAdjustmentsOnComplete; }
}
