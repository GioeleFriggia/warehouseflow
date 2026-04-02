package com.gioele.warehouseflow.dto;

import java.util.ArrayList;
import java.util.List;

public class DashboardResponse {
    private long totalProducts;
    private long lowStockProducts;
    private long movementsToday;
    private long openOrders;
    private List<StockMovementResponse> recentMovements = new ArrayList<>();
    private List<LowStockAlertResponse> lowStockAlerts = new ArrayList<>();

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
    public long getLowStockProducts() { return lowStockProducts; }
    public void setLowStockProducts(long lowStockProducts) { this.lowStockProducts = lowStockProducts; }
    public long getMovementsToday() { return movementsToday; }
    public void setMovementsToday(long movementsToday) { this.movementsToday = movementsToday; }
    public long getOpenOrders() { return openOrders; }
    public void setOpenOrders(long openOrders) { this.openOrders = openOrders; }
    public List<StockMovementResponse> getRecentMovements() { return recentMovements; }
    public void setRecentMovements(List<StockMovementResponse> recentMovements) { this.recentMovements = recentMovements; }
    public List<LowStockAlertResponse> getLowStockAlerts() { return lowStockAlerts; }
    public void setLowStockAlerts(List<LowStockAlertResponse> lowStockAlerts) { this.lowStockAlerts = lowStockAlerts; }
}
