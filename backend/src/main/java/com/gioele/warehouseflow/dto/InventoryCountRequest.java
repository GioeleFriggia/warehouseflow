package com.gioele.warehouseflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryCountRequest {

    @NotNull
    private Long itemId;

    @Min(0)
    private int countedQuantity;

    private String notes;

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public int getCountedQuantity() { return countedQuantity; }
    public void setCountedQuantity(int countedQuantity) { this.countedQuantity = countedQuantity; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
