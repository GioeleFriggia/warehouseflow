package com.gioele.warehouseflow.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "inventory_sessions")
public class InventorySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(length = 120)
    private String categoryFilter;

    @Column(length = 120)
    private String supplierFilter;

    @Column(length = 120)
    private String locationFilter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InventorySessionStatus status = InventorySessionStatus.OPEN;

    @Column(length = 500)
    private String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false)
    private boolean applyAdjustmentsOnComplete = false;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventorySessionItem> items = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime completedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

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
    public InventorySessionStatus getStatus() { return status; }
    public void setStatus(InventorySessionStatus status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public boolean isApplyAdjustmentsOnComplete() { return applyAdjustmentsOnComplete; }
    public void setApplyAdjustmentsOnComplete(boolean applyAdjustmentsOnComplete) { this.applyAdjustmentsOnComplete = applyAdjustmentsOnComplete; }
    public List<InventorySessionItem> getItems() { return items; }
    public void setItems(List<InventorySessionItem> items) { this.items = items; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
