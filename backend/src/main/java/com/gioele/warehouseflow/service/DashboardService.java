package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.DashboardResponse;
import com.gioele.warehouseflow.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final StockService stockService;
    private final OrderService orderService;

    public DashboardService(ProductRepository productRepository,
                            StockService stockService,
                            OrderService orderService) {
        this.productRepository = productRepository;
        this.stockService = stockService;
        this.orderService = orderService;
    }

    public DashboardResponse getDashboard() {
        DashboardResponse response = new DashboardResponse();
        response.setTotalProducts(productRepository.count());
        response.setLowStockProducts(productRepository.findAll().stream()
                .filter(product -> product.getQuantityAvailable() <= product.getMinimumThreshold())
                .count());
        response.setMovementsToday(stockService.countToday());
        response.setOpenOrders(orderService.countOpenOrders());
        response.setRecentMovements(stockService.findRecent());
        return response;
    }
}
