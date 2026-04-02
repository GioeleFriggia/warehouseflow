package com.gioele.warehouseflow.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

public class OrderReceiveRequest {

    @Valid
    @NotEmpty
    private List<OrderReceiveItemRequest> items = new ArrayList<>();

    private String notes;

    public List<OrderReceiveItemRequest> getItems() { return items; }
    public void setItems(List<OrderReceiveItemRequest> items) { this.items = items; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
