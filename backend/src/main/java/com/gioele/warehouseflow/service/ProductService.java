package com.gioele.warehouseflow.service;

import com.gioele.warehouseflow.dto.ProductRequest;
import com.gioele.warehouseflow.dto.ProductResponse;
import com.gioele.warehouseflow.entity.Product;
import com.gioele.warehouseflow.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductResponse> findAll(String search) {
        List<Product> products;
        if (StringUtils.hasText(search)) {
            products = productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(search, search);
        } else {
            products = productRepository.findAll();
        }
        return products.stream().map(this::toResponse).toList();
    }

    public List<ProductResponse> lowStock() {
        return productRepository.findAll().stream()
                .filter(product -> product.getQuantityAvailable() <= product.getMinimumThreshold())
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse create(ProductRequest request) {
        if (productRepository.findBySku(request.getSku()).isPresent()) {
            throw new ResponseStatusException(BAD_REQUEST, "SKU già esistente");
        }
        Product product = new Product();
        mapRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prodotto non trovato"));
        Product existing = productRepository.findBySku(request.getSku()).orElse(null);
        if (existing != null && !existing.getId().equals(id)) {
            throw new ResponseStatusException(BAD_REQUEST, "SKU già esistente");
        }
        mapRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public Product getEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Prodotto non trovato"));
    }

    public Product save(Product product) {
        return productRepository.save(product);
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
}
