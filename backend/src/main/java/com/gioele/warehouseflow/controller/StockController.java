package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.StockMovementRequest;
import com.gioele.warehouseflow.dto.StockMovementResponse;
import com.gioele.warehouseflow.entity.MovementType;
import com.gioele.warehouseflow.service.StockService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/movements")
    public List<StockMovementResponse> recentMovements(@RequestParam(required = false) Long productId,
                                                       @RequestParam(required = false) MovementType movementType,
                                                       @RequestParam(required = false) String performedBy,
                                                       @RequestParam(required = false) LocalDate dateFrom,
                                                       @RequestParam(required = false) LocalDate dateTo) {
        if (productId != null || movementType != null || performedBy != null || dateFrom != null || dateTo != null) {
            return stockService.findFiltered(productId, movementType, performedBy, dateFrom, dateTo);
        }
        return stockService.findRecent();
    }

    @GetMapping("/movements/today")
    public List<StockMovementResponse> todayMovements() {
        return stockService.findToday();
    }

    @PostMapping("/movements")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE','STORE_OPERATOR')")
    public StockMovementResponse registerMovement(@Valid @RequestBody StockMovementRequest request) {
        return stockService.registerMovement(request);
    }
}
