package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.LowStockAlertResponse;
import com.gioele.warehouseflow.dto.ProductRequest;
import com.gioele.warehouseflow.dto.ProductResponse;
import com.gioele.warehouseflow.entity.AuditAction;
import com.gioele.warehouseflow.entity.Product;
import com.gioele.warehouseflow.entity.User;
import com.gioele.warehouseflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;
    private final UserService userService;

    public ProductService(ProductRepository productRepository,
                          AuditLogService auditLogService,
                          UserService userService) {
        this.productRepository = productRepository;
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    public List<ProductResponse> findAll(String search) {
        return findAll(search, null, null, false);
    }

    public List<ProductResponse> findAll(String search, String category, String supplier, Boolean lowStockOnly) {
        return findEntities(search, category, supplier, Boolean.TRUE.equals(lowStockOnly)).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<Product> findEntities(String search, String category, String supplier, boolean lowStockOnly) {
        return productRepository.findAll().stream()
                .filter(product -> !StringUtils.hasText(search)
                        || product.getName().toLowerCase().contains(search.toLowerCase())
                        || product.getSku().toLowerCase().contains(search.toLowerCase()))
                .filter(product -> !StringUtils.hasText(category)
                        || (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)))
                .filter(product -> !StringUtils.hasText(supplier)
                        || (product.getSupplier() != null && product.getSupplier().equalsIgnoreCase(supplier)))
                .filter(product -> !lowStockOnly || product.getQuantityAvailable() <= product.getMinimumThreshold())
                .sorted(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<ProductResponse> lowStock() {
        return lowStockAlerts().stream().map(this::toResponse).toList();
    }

    private ProductResponse toResponse(LowStockAlertResponse alert) {
        Product product = getEntity(alert.getProductId());
        return toResponse(product);
    }

    public List<LowStockAlertResponse> lowStockAlerts() {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantityAvailable() <= product.getMinimumThreshold())
                .sorted(Comparator.comparingInt(Product::getQuantityAvailable))
                .map(product -> {
                    LowStockAlertResponse response = new LowStockAlertResponse();
                    response.setProductId(product.getId());
                    response.setSku(product.getSku());
                    response.setProductName(product.getName());
                    response.setCategory(product.getCategory());
                    response.setSupplier(product.getSupplier());
                    response.setQuantityAvailable(product.getQuantityAvailable());
                    response.setMinimumThreshold(product.getMinimumThreshold());
                    response.setMissingQuantity(Math.max(0, product.getMinimumThreshold() - product.getQuantityAvailable()));
                    return response;
                })
                .toList();
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "SKU già esistente");
        }
        Product product = new Product();
        mapRequest(product, request);
        Product saved = productRepository.save(product);
        User currentUser = userService.getCurrentUserEntity();
        auditLogService.log(AuditAction.PRODUCT_CREATED, "Product", String.valueOf(saved.getId()), currentUser,
                null, compact(saved), "Creazione prodotto");
        return toResponse(saved);
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prodotto non trovato"));
        Product existing = productRepository.findBySku(request.getSku()).orElse(null);
        if (existing != null && !existing.getId().equals(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "SKU già esistente");
        }
        String oldValue = compact(product);
        mapRequest(product, request);
        Product saved = productRepository.save(product);
        User currentUser = userService.getCurrentUserEntity();
        auditLogService.log(AuditAction.PRODUCT_UPDATED, "Product", String.valueOf(saved.getId()), currentUser,
                oldValue, compact(saved), "Aggiornamento prodotto");
        return toResponse(saved);
    }

    public Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prodotto non trovato"));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public long countAll() {
        return productRepository.count();
    }

    private void mapRequest(Product product, ProductRequest request) {
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setUnitOfMeasure(request.getUnitOfMeasure());
        product.setMinimumThreshold(request.getMinimumThreshold());
        product.setSupplier(request.getSupplier());
        product.setWarehouseLocation(request.getWarehouseLocation());
        product.setActive(request.isActive());
    }

    public ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setSku(product.getSku());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setCategory(product.getCategory());
        response.setUnitOfMeasure(product.getUnitOfMeasure());
        response.setQuantityAvailable(product.getQuantityAvailable());
        response.setMinimumThreshold(product.getMinimumThreshold());
        response.setSupplier(product.getSupplier());
        response.setWarehouseLocation(product.getWarehouseLocation());
        response.setActive(product.isActive());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    private String compact(Product product) {
        return "sku=" + product.getSku()
                + ",name=" + product.getName()
                + ",qty=" + product.getQuantityAvailable()
                + ",min=" + product.getMinimumThreshold()
                + ",supplier=" + product.getSupplier()
                + ",category=" + product.getCategory();
    }
}
