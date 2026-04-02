package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.ProductResponse;
import com.gioele.warehouseflow.dto.PurchaseOrderResponse;
import com.gioele.warehouseflow.dto.StockMovementResponse;
import com.gioele.warehouseflow.entity.MovementType;
import com.gioele.warehouseflow.entity.OrderStatus;
import com.gioele.warehouseflow.service.ExportService;
import com.gioele.warehouseflow.service.OrderService;
import com.gioele.warehouseflow.service.ProductService;
import com.gioele.warehouseflow.service.StockService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/exports")
@CrossOrigin
public class ExportController {

    private final ProductService productService;
    private final StockService stockService;
    private final OrderService orderService;
    private final ExportService exportService;

    public ExportController(ProductService productService,
                            StockService stockService,
                            OrderService orderService,
                            ExportService exportService) {
        this.productService = productService;
        this.stockService = stockService;
        this.orderService = orderService;
        this.exportService = exportService;
    }

    @GetMapping("/products.xlsx")
    public ResponseEntity<byte[]> exportProductsExcel(@RequestParam(required = false) String search,
                                                      @RequestParam(required = false) String category,
                                                      @RequestParam(required = false) String supplier,
                                                      @RequestParam(required = false) Boolean lowStockOnly) {
        List<ProductResponse> data = productService.findAll(search, category, supplier, lowStockOnly);
        return xlsx("prodotti.xlsx", exportService.exportProductsExcel(data));
    }

    @GetMapping("/products.pdf")
    public ResponseEntity<byte[]> exportProductsPdf(@RequestParam(required = false) String search,
                                                    @RequestParam(required = false) String category,
                                                    @RequestParam(required = false) String supplier,
                                                    @RequestParam(required = false) Boolean lowStockOnly) {
        List<ProductResponse> data = productService.findAll(search, category, supplier, lowStockOnly);
        return pdf("prodotti.pdf", exportService.exportProductsPdf(data));
    }

    @GetMapping("/movements.xlsx")
    public ResponseEntity<byte[]> exportMovementsExcel(@RequestParam(required = false) Long productId,
                                                       @RequestParam(required = false) MovementType movementType,
                                                       @RequestParam(required = false) String performedBy,
                                                       @RequestParam(required = false) LocalDate dateFrom,
                                                       @RequestParam(required = false) LocalDate dateTo) {
        List<StockMovementResponse> data = stockService.findFiltered(productId, movementType, performedBy, dateFrom, dateTo);
        return xlsx("movimenti.xlsx", exportService.exportMovementsExcel(data));
    }

    @GetMapping("/movements.pdf")
    public ResponseEntity<byte[]> exportMovementsPdf(@RequestParam(required = false) Long productId,
                                                     @RequestParam(required = false) MovementType movementType,
                                                     @RequestParam(required = false) String performedBy,
                                                     @RequestParam(required = false) LocalDate dateFrom,
                                                     @RequestParam(required = false) LocalDate dateTo) {
        List<StockMovementResponse> data = stockService.findFiltered(productId, movementType, performedBy, dateFrom, dateTo);
        return pdf("movimenti.pdf", exportService.exportMovementsPdf(data));
    }

    @GetMapping("/orders.xlsx")
    public ResponseEntity<byte[]> exportOrdersExcel(@RequestParam(required = false) String supplier,
                                                    @RequestParam(required = false) OrderStatus status,
                                                    @RequestParam(required = false) LocalDate dateFrom,
                                                    @RequestParam(required = false) LocalDate dateTo) {
        List<PurchaseOrderResponse> data = orderService.findFiltered(supplier, status, dateFrom, dateTo);
        return xlsx("ordini.xlsx", exportService.exportOrdersExcel(data));
    }

    @GetMapping("/orders.pdf")
    public ResponseEntity<byte[]> exportOrdersPdf(@RequestParam(required = false) String supplier,
                                                  @RequestParam(required = false) OrderStatus status,
                                                  @RequestParam(required = false) LocalDate dateFrom,
                                                  @RequestParam(required = false) LocalDate dateTo) {
        List<PurchaseOrderResponse> data = orderService.findFiltered(supplier, status, dateFrom, dateTo);
        return pdf("ordini.pdf", exportService.exportOrdersPdf(data));
    }

    private ResponseEntity<byte[]> xlsx(String fileName, byte[] body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
        return ResponseEntity.ok().headers(headers).body(body);
    }

    private ResponseEntity<byte[]> pdf(String fileName, byte[] body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
        return ResponseEntity.ok().headers(headers).body(body);
    }
}
