package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.StockMovementResponse;
import com.gioele.warehouseflow.entity.AuditAction;
import com.gioele.warehouseflow.dto.StockMovementRequest;
import com.gioele.warehouseflow.entity.MovementType;
import com.gioele.warehouseflow.entity.Product;
import com.gioele.warehouseflow.entity.StockMovement;
import com.gioele.warehouseflow.entity.User;
import com.gioele.warehouseflow.repository.StockMovementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class StockService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductService productService;
    private final UserService userService;
    private final AuditLogService auditLogService;

    public StockService(StockMovementRepository stockMovementRepository,
                        ProductService productService,
                        UserService userService,
                        AuditLogService auditLogService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productService = productService;
        this.userService = userService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public StockMovementResponse registerMovement(StockMovementRequest request) {
        Product product = productService.getEntity(request.getProductId());
        User user = userService.getCurrentUserEntity();

        int currentQuantity = product.getQuantityAvailable();
        int nextQuantity;

        if (request.getMovementType() == MovementType.INBOUND) {
            nextQuantity = currentQuantity + request.getQuantity();
        } else if (request.getMovementType() == MovementType.OUTBOUND) {
            nextQuantity = currentQuantity - request.getQuantity();
        } else {
            nextQuantity = request.getQuantity();
        }

        if (nextQuantity < 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Quantità insufficiente in magazzino");
        }

        product.setQuantityAvailable(nextQuantity);
        productService.save(product);

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setPerformedBy(user);
        movement.setMovementType(request.getMovementType());
        movement.setQuantity(request.getQuantity());
        movement.setSourceLocation(request.getSourceLocation());
        movement.setDestinationLocation(request.getDestinationLocation());
        movement.setNotes(request.getNotes());

        StockMovement saved = stockMovementRepository.save(movement);
        auditLogService.log(AuditAction.STOCK_MOVEMENT_CREATED, "StockMovement", String.valueOf(saved.getId()), user,
                "qtyBefore=" + currentQuantity, "qtyAfter=" + nextQuantity,
                saved.getMovementType() + " " + saved.getQuantity() + " " + product.getSku());

        return toResponse(saved);
    }

    public List<StockMovementResponse> findRecent() {
        return stockMovementRepository.findTop50ByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<StockMovementResponse> findFiltered(Long productId,
                                                    MovementType movementType,
                                                    String performedBy,
                                                    LocalDate dateFrom,
                                                    LocalDate dateTo) {
        LocalDateTime from = dateFrom != null ? dateFrom.atStartOfDay() : null;
        LocalDateTime to = dateTo != null ? dateTo.plusDays(1).atStartOfDay() : null;

        return stockMovementRepository.findAll().stream()
                .filter(movement -> productId == null || movement.getProduct().getId().equals(productId))
                .filter(movement -> movementType == null || movement.getMovementType() == movementType)
                .filter(movement -> !StringUtils.hasText(performedBy)
                        || (movement.getPerformedBy().getFirstName() + " " + movement.getPerformedBy().getLastName()).toLowerCase().contains(performedBy.toLowerCase()))
                .filter(movement -> from == null || !movement.getCreatedAt().isBefore(from))
                .filter(movement -> to == null || movement.getCreatedAt().isBefore(to))
                .sorted(Comparator.comparing(StockMovement::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public List<StockMovementResponse> findToday() {
        LocalDate today = LocalDate.now();
        return findFiltered(null, null, null, today, today);
    }

    public long countToday() {
        LocalDate today = LocalDate.now();
        return stockMovementRepository.countByCreatedAtBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    public StockMovementResponse toResponse(StockMovement movement) {
        StockMovementResponse response = new StockMovementResponse();
        response.setId(movement.getId());
        response.setProductId(movement.getProduct().getId());
        response.setProductName(movement.getProduct().getName());
        response.setSku(movement.getProduct().getSku());
        response.setMovementType(movement.getMovementType());
        response.setQuantity(movement.getQuantity());
        response.setSourceLocation(movement.getSourceLocation());
        response.setDestinationLocation(movement.getDestinationLocation());
        response.setNotes(movement.getNotes());
        response.setPerformedBy(movement.getPerformedBy().getFirstName() + " " + movement.getPerformedBy().getLastName());
        response.setCreatedAt(movement.getCreatedAt());
        return response;
    }
}
