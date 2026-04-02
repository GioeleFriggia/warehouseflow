package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.DashboardResponse;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProductService productService;
    private final StockService stockService;
    private final OrderService orderService;

    public DashboardService(ProductService productService,
                            StockService stockService,
                            OrderService orderService) {
        this.productService = productService;
        this.stockService = stockService;
        this.orderService = orderService;
    }

    public DashboardResponse getDashboard() {
        DashboardResponse response = new DashboardResponse();
        response.setTotalProducts(productService.countAll());
        response.setLowStockProducts(productService.lowStockAlerts().size());
        response.setMovementsToday(stockService.countToday());
        response.setOpenOrders(orderService.countOpenOrders());
        response.setRecentMovements(stockService.findRecent());
        response.setLowStockAlerts(productService.lowStockAlerts());
        return response;
    }
}
