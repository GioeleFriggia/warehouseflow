package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.PurchaseOrderDocumentResponse;
import com.gioele.warehouseflow.entity.PurchaseOrder;
import com.gioele.warehouseflow.entity.PurchaseOrderDocument;
import com.gioele.warehouseflow.entity.User;
import com.gioele.warehouseflow.repository.PurchaseOrderDocumentRepository;
import com.gioele.warehouseflow.repository.PurchaseOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PurchaseOrderDocumentService {

    private final PurchaseOrderDocumentRepository documentRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final UserService userService;

    public PurchaseOrderDocumentService(PurchaseOrderDocumentRepository documentRepository,
                                        PurchaseOrderRepository purchaseOrderRepository,
                                        UserService userService) {
        this.documentRepository = documentRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.userService = userService;
    }

    @Transactional
    public PurchaseOrderDocumentResponse upload(Long orderId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "File non valido");
        }

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Ordine non trovato"));
        User user = userService.getCurrentUserEntity();

        try {
            PurchaseOrderDocument document = new PurchaseOrderDocument();
            document.setPurchaseOrder(order);
            document.setFileName(file.getOriginalFilename() == null ? "documento" : file.getOriginalFilename());
            document.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
            document.setData(file.getBytes());
            document.setUploadedBy(user);
            return toResponse(documentRepository.save(document));
        } catch (IOException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Impossibile leggere il file");
        }
    }

    public List<PurchaseOrderDocumentResponse> findByOrder(Long orderId) {
        return documentRepository.findByPurchaseOrderIdOrderByUploadedAtDesc(orderId).stream()
                .map(this::toResponse)
                .toList();
    }

    public PurchaseOrderDocument getEntity(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Documento non trovato"));
    }

    private PurchaseOrderDocumentResponse toResponse(PurchaseOrderDocument document) {
        PurchaseOrderDocumentResponse response = new PurchaseOrderDocumentResponse();
        response.setId(document.getId());
        response.setFileName(document.getFileName());
        response.setContentType(document.getContentType());
        response.setUploadedAt(document.getUploadedAt());
        response.setUploadedBy(document.getUploadedBy().getFirstName() + " " + document.getUploadedBy().getLastName());
        return response;
    }
}
