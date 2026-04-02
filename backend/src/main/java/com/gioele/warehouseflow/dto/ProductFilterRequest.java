package com.gioele.warehouseflow.dto;

public class ProductFilterRequest {
    private String search;
    private String category;
    private String supplier;
    private Boolean lowStockOnly;

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public Boolean getLowStockOnly() { return lowStockOnly; }
    public void setLowStockOnly(Boolean lowStockOnly) { this.lowStockOnly = lowStockOnly; }
}
