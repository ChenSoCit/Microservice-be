package com.example.product_service.services.impl;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.product_service.clients.OrderDetailClient;
import com.example.product_service.clients.UserClient;
import com.example.product_service.dtos.request.ProductRequest;
import com.example.product_service.dtos.response.ApiResponse;
import com.example.product_service.dtos.response.ProductPageResponse;
import com.example.product_service.dtos.response.ProductResponse;
import com.example.product_service.dtos.response.TopProductStatResponse;
import com.example.product_service.dtos.response.TopSellingProductResponse;
import com.example.product_service.exceptions.BadRequestException;
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
    private final UserClient userClient;
    private final OrderDetailClient orderDetailClient;
    private final ProductMapStruct productMapstruct;

    @Override
    @Transactional
    public int insertProduct(ProductRequest request) {
        log.info("Inserting product: {}", request);
        
        // mapstruct
        Product product = productMapstruct.toProduct(request);
              
        return productMapper.insertProduct(product);
    }

    @Override
    public ProductResponse getProductById(Integer id) {
        log.info("Getting product by ID: {}", id);

        ProductResponse product = productMapper.getProductById(id);

        return product;
    }

    @Override
    public ProductResponse upDateProduct(Integer id, ProductRequest request) {
        log.info("Updating product: {}", request);

        Product upDateProduct = Product.builder()
                .id(id)
                .nameProduct(request.getNameProduct())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .categoryId(request.getCategoryId())
                .build();
        
        productMapper.upDateProduct(upDateProduct);
        return productMapper.getProductById(id);
    
    }

    @Override
    public int deleteProduct(Integer id) {
        int deletedCount = productMapper.deleteProduct(id);

        if (deletedCount == 0) {
            log.warn("No product found with ID={} to delete", id);
            throw new BadRequestException("Product not found with id=" + id);
        }
        return deletedCount;
    }

    @Override
    @Transactional
    public void decreaseStockProduct(Integer productId, Integer quantity) {
        ProductResponse product = productMapper.getProductById(productId);
        int currentStock = product.getStockQuantity();

        if (currentStock < quantity) {
            throw new IllegalArgumentException("Not enough stock for product " + productId);
        }

        productMapper.decreaseStock(productId, quantity);
    }

    @Override
    public void increaseStockProduct(Integer productId, Integer quantity) {

        productMapper.increaseStock(productId, quantity);
    }

    @Override
    public List<TopSellingProductResponse> getTopSellingProducts() {
        // 1. Gọi sang order-detail-service để lấy thống kê top sản phẩm
        ApiResponse<List<TopProductStatResponse>> response = orderDetailClient.getTopProduct();
    
        List<TopProductStatResponse> stats = response.getData();
        
        // 2. Join thêm thông tin từ bảng product
        List<TopSellingProductResponse> result = new ArrayList<>();
        for(TopProductStatResponse stat : stats){
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
            }
        }
        return result;
        
    }

    @Override
    public ProductPageResponse searchProduct(String keyword, int page, int size) {
        
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
