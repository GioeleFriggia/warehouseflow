package com.gioele.warehouseflow.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_snapshot_items")
public class InventorySnapshotItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "snapshot_id")
    private InventorySnapshot snapshot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantityAvailable;

    @Column(nullable = false)
    private int minimumThreshold;

    @Column(length = 80)
    private String category;

    @Column(length = 120)
    private String supplier;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InventorySnapshot getSnapshot() { return snapshot; }
    public void setSnapshot(InventorySnapshot snapshot) { this.snapshot = snapshot; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public int getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(int quantityAvailable) { this.quantityAvailable = quantityAvailable; }

    public int getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(int minimumThreshold) { this.minimumThreshold = minimumThreshold; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
}
