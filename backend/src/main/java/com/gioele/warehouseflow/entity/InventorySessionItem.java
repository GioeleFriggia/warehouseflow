package com.gioele.warehouseflow.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_session_items")
public class InventorySessionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "session_id")
    private InventorySession session;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int systemQuantity;

    private Integer countedQuantity;

    @Column(length = 500)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "counted_by")
    private User countedBy;

    private LocalDateTime countedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public InventorySession getSession() { return session; }
    public void setSession(InventorySession session) { this.session = session; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public int getSystemQuantity() { return systemQuantity; }
    public void setSystemQuantity(int systemQuantity) { this.systemQuantity = systemQuantity; }
    public Integer getCountedQuantity() { return countedQuantity; }
    public void setCountedQuantity(Integer countedQuantity) { this.countedQuantity = countedQuantity; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public User getCountedBy() { return countedBy; }
    public void setCountedBy(User countedBy) { this.countedBy = countedBy; }
    public LocalDateTime getCountedAt() { return countedAt; }
    public void setCountedAt(LocalDateTime countedAt) { this.countedAt = countedAt; }
}
