package com.gioele.warehouseflow.controller;

import com.gioele.warehouseflow.dto.*;
import com.gioele.warehouseflow.entity.PurchaseOrderDocument;
import com.gioele.warehouseflow.service.OrderService;
import com.gioele.warehouseflow.service.PurchaseOrderDocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    private final PurchaseOrderDocumentService documentService;

    public OrderController(OrderService orderService, PurchaseOrderDocumentService documentService) {
        this.orderService = orderService;
        this.documentService = documentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<PurchaseOrderResponse> findAll() {
        return orderService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PurchaseOrderResponse create(@Valid @RequestBody PurchaseOrderRequest request) {
        return orderService.create(request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public PurchaseOrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody PurchaseOrderStatusRequest request) {
        return orderService.updateStatus(id, request);
    }

    @PostMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public PurchaseOrderResponse receive(@PathVariable Long id, @Valid @RequestBody OrderReceiveRequest request) {
        return orderService.receiveOrder(id, request);
    }

    @GetMapping("/{id}/documents")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public List<PurchaseOrderDocumentResponse> documents(@PathVariable Long id) {
        return documentService.findByOrder(id);
    }

    @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public PurchaseOrderDocumentResponse uploadDocument(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        return documentService.upload(id, file);
    }

    @GetMapping("/documents/{documentId}/download")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','WAREHOUSE')")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable Long documentId) {
        PurchaseOrderDocument document = documentService.getEntity(documentId);
        ByteArrayResource resource = new ByteArrayResource(document.getData());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(document.getContentType()))
                .contentLength(document.getData().length)
                .body(resource);
    }
}
