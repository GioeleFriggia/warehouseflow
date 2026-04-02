package com.gioele.warehouseflow.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ProductRequest {
    @NotBlank
    private String sku;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String category;
    @NotBlank
    private String unitOfMeasure;
    @Min(0)
    private int minimumThreshold;
    private String supplier;
    private String warehouseLocation;
    private boolean active = true;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }
    public int getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(int minimumThreshold) { this.minimumThreshold = minimumThreshold; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public String getWarehouseLocation() { return warehouseLocation; }
    public void setWarehouseLocation(String warehouseLocation) { this.warehouseLocation = warehouseLocation; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
