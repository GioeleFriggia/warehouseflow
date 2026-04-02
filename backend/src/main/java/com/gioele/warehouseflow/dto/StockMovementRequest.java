package com.gioele.warehouseflow.dto;

import com.gioele.warehouseflow.entity.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockMovementRequest {
    @NotNull
    private Long productId;
    @NotNull
    private MovementType movementType;
    @Min(1)
    private int quantity;
    private String sourceLocation;
    private String destinationLocation;
    private String notes;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public MovementType getMovementType() { return movementType; }
    public void setMovementType(MovementType movementType) { this.movementType = movementType; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getSourceLocation() { return sourceLocation; }
    public void setSourceLocation(String sourceLocation) { this.sourceLocation = sourceLocation; }
    public String getDestinationLocation() { return destinationLocation; }
    public void setDestinationLocation(String destinationLocation) { this.destinationLocation = destinationLocation; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
