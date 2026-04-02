package com.gioele.warehouseflow.dto;

public class LowStockAlertResponse {
    private Long productId;
    private String sku;
    private String productName;
    private String category;
    private String supplier;
    private int quantityAvailable;
    private int minimumThreshold;
    private int missingQuantity;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public int getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(int minimumThreshold) { this.minimumThreshold = minimumThreshold; }

    public int getMissingQuantity() { return missingQuantity; }
    public void setMissingQuantity(int missingQuantity) { this.missingQuantity = missingQuantity; }
}
