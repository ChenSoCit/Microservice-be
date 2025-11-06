package com.example.product_service.services.impl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.product_service.clients.OrderDetailClient;
import com.example.product_service.dtos.request.ProductRequest;
import com.example.product_service.dtos.response.ApiResponse;
import com.example.product_service.dtos.response.ProductPageResponse;
import com.example.product_service.dtos.common_dto.ProductResponse;
import com.example.product_service.dtos.response.TopProductStatResponse;
import com.example.product_service.dtos.response.TopSellingProductResponse;
import com.example.product_service.exceptions.BadRequestException;
import com.example.product_service.exceptions.DatabaseOperationException;
import com.example.product_service.exceptions.ResourceNotFoundException;
import com.example.product_service.mappers.ProductMapper;
import com.example.product_service.mapstruct.ProductMapStruct;
import com.example.product_service.models.Product;
import com.example.product_service.services.ProductService;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "PRODUCT_SERVICE")
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductMapper productMapper;
    private final OrderDetailClient orderDetailClient;
    private final ProductMapStruct productMapstruct;

    @Override
    @Transactional
    public int insertProduct(ProductRequest request) {
        log.info("Inserting product: {}", request);
        try {
            // mapstruct
            Product product = productMapstruct.toProduct(request);
            int result = productMapper.insertProduct(product);
            
            if (result == 0 || product.getId() == null) {
                log.error("Failed to insert product: {}", request);
                throw new DatabaseOperationException("Failed to insert product into database");
            }
            
            return product.getId();
        } catch (DatabaseOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inserting product: {}", request, e);
            throw new DatabaseOperationException("Database error while inserting product: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductResponse getProductById(int id) {
        log.info("Getting product by ID: {}", id);
        ProductResponse product = productMapper.getProductById(id);
        if (product == null) {
            log.error("Product not found with ID: {}", id);
            throw new ResourceNotFoundException("Product not found with id=" + id);
        }
        return product;
    }

    @Override
    public ProductResponse upDateProduct(Integer id, ProductRequest request) {
        log.info("Updating product: {}", request);

        // Kiểm tra product có tồn tại không
        ProductResponse existingProduct = productMapper.getProductById(id);
        if (existingProduct == null) {
            log.error("Product not found with ID: {}", id);
            throw new ResourceNotFoundException("Product not found with id=" + id);
        }

        try {
            Product upDateProduct = Product.builder()
                    .id(id)
                    .nameProduct(request.getNameProduct())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .stockQuantity(request.getStockQuantity())
                    .categoryId(request.getCategoryId())
                    .build();
            
            int updatedRows = productMapper.upDateProduct(upDateProduct);
            if (updatedRows == 0) {
                log.error("Failed to update product with ID: {}", id);
                throw new DatabaseOperationException("Failed to update product with id=" + id);
            }
            
            return productMapper.getProductById(id);
        } catch (DatabaseOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating product with ID: {}", id, e);
            throw new DatabaseOperationException("Database error while updating product: " + e.getMessage(), e);
        }
    
    }

    @Override
    public int deleteProduct(Integer id) {
        try {
            int deletedCount = productMapper.deleteProduct(id);

            if (deletedCount == 0) {
                log.warn("No product found with ID={} to delete", id);
                throw new ResourceNotFoundException("Product not found with id=" + id);
            }
            return deletedCount;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting product with ID: {}", id, e);
            throw new DatabaseOperationException("Database error while deleting product: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void decreaseStockProduct(int productId, int quantity) {
        if (quantity <= 0) {
            log.error("Invalid quantity: {}", quantity);
            throw new BadRequestException("Quantity must be greater than 0");
        }

        // Kiểm tra product có tồn tại và có đủ stock không
        ProductResponse product = productMapper.getProductById(productId);
        if (product == null) {
            log.error("Product not found with ID: {}", productId);
            throw new ResourceNotFoundException("Product not found with id=" + productId);
        }

        if (product.getStockQuantity() < quantity) {
            log.error("Insufficient stock for product ID: {}. Available: {}, Requested: {}", 
                     productId, product.getStockQuantity(), quantity);
            throw new BadRequestException(
                String.format("Insufficient stock for product id=%d. Available: %d, Requested: %d", 
                             productId, product.getStockQuantity(), quantity));
        }

        try {
            int updatedRows = productMapper.decreaseStock(productId, quantity);
            if (updatedRows == 0) {
                log.error("Failed to decrease stock for product ID: {}", productId);
                throw new DatabaseOperationException("Failed to decrease stock for product id=" + productId);
            }
        } catch (DatabaseOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error decreasing stock for product ID: {}", productId, e);
            throw new DatabaseOperationException("Database error while decreasing stock: " + e.getMessage(), e);
        }
    }

    @Override
    public void increaseStockProduct(Integer productId, Integer quantity) {
        if (quantity <= 0) {
            log.error("Invalid quantity: {}", quantity);
            throw new BadRequestException("Quantity must be greater than 0");
        }

        // Kiểm tra product có tồn tại không
        ProductResponse product = productMapper.getProductById(productId);
        if (product == null) {
            log.error("Product not found with ID: {}", productId);
            throw new ResourceNotFoundException("Product not found with id=" + productId);
        }

        try {
            int updatedRows = productMapper.increaseStock(productId, quantity);
            if (updatedRows == 0) {
                log.error("Failed to increase stock for product ID: {}", productId);
                throw new DatabaseOperationException("Failed to increase stock for product id=" + productId);
            }
        } catch (DatabaseOperationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error increasing stock for product ID: {}", productId, e);
            throw new DatabaseOperationException("Database error while increasing stock: " + e.getMessage(), e);
        }
    }

    @Override
    public List<TopSellingProductResponse> getTopSellingProducts() {
        try {
            // 1. Gọi sang order-detail-service để lấy thống kê top sản phẩm
            ApiResponse<List<TopProductStatResponse>> response = orderDetailClient.getTopProduct();
        
            if (response == null || response.getData() == null) {
                log.warn("No data received from order-detail-service");
                return new ArrayList<>();
            }

            List<TopProductStatResponse> stats = response.getData();
            
            // 2. Join thêm thông tin từ bảng product
            List<TopSellingProductResponse> result = new ArrayList<>();
            for(TopProductStatResponse stat : stats){
                try {
                    ProductResponse product = productMapper.getProductById(stat.getProductId());
                    if(product != null){
                        TopSellingProductResponse item = TopSellingProductResponse.builder()
                            .productId(product.getId())
                            .nameProduct(product.getNameProduct())
                            .categoryId(product.getCategoryId())
                            .price(product.getPrice())
                            .totalSold(stat.getTotalSold())
                        .build();
                        result.add(item);
                    } else {
                        log.warn("Product not found for productId: {}", stat.getProductId());
                    }
                } catch (Exception e) {
                    log.error("Error processing product ID: {}", stat.getProductId(), e);
                    // Continue với product tiếp theo
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error calling order-detail-service", e);
            throw new BadRequestException("Failed to get top selling products: " + e.getMessage());
        }
        
    }

    @Override
    public ProductPageResponse searchProduct(String keyword, int page, int size) {
        if (page < 1) {
            log.error("Invalid page number: {}", page);
            throw new BadRequestException("Page number must be greater than 0");
        }
        if (size < 1 || size > 100) {
            log.error("Invalid page size: {}", size);
            throw new BadRequestException("Page size must be between 1 and 100");
        }
        
        int offset = (page - 1) * size;
        String safeKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        long totalElements = productMapper.countByName(safeKeyword);
        List<ProductResponse> productPage = productMapper.searchProduct(safeKeyword, size, offset);
        int totalPages = (int) Math.ceil((double)totalElements/size);
        
        ProductPageResponse productPageResponse = new ProductPageResponse();
        productPageResponse.setPageNumber(page);
        productPageResponse.setPageSize(size);
        productPageResponse.setTotalElements(totalElements);
        productPageResponse.setTotalPages(totalPages);
        productPageResponse.setProduct(productPage);

        return productPageResponse;
       
    }

    @Override
    public ProductPageResponse findByPrice(String sort,int page, int size) {
        if (page < 1) {
            log.error("Invalid page number: {}", page);
            throw new BadRequestException("Page number must be greater than 0");
        }
        if (size < 1 || size > 100) {
            log.error("Invalid page size: {}", size);
            throw new BadRequestException("Page size must be between 1 and 100");
        }

        int offset = (page - 1) * size;

        String sortdir = (sort == null || !"DESC".equalsIgnoreCase(sort)) ? "ASC" : "DESC";
        long totalElements = productMapper.countAll();
        List<ProductResponse> productPage = productMapper.findAllByPrice(sortdir, size, offset);
        int totalPages = (int) Math.ceil((double)totalElements/size);

        ProductPageResponse productPageResponse = new ProductPageResponse();
        productPageResponse.setPageNumber(page);
        productPageResponse.setPageSize(size);
        productPageResponse.setTotalElements(totalElements);
        productPageResponse.setTotalPages(totalPages);
        productPageResponse.setProduct(productPage);

        return productPageResponse;
    }

    
    


}
