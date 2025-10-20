package com.example.product_service.services;

import java.util.List;

import com.example.product_service.dtos.request.ProductRequest;
import com.example.product_service.dtos.response.PageResponse;
import com.example.product_service.dtos.response.ProductPageResponse;
import com.example.product_service.dtos.response.ProductResponse;
import com.example.product_service.dtos.response.TopSellingProductResponse;



public interface  ProductService {
    int insertProduct(ProductRequest request);
 
    ProductResponse getProductById(Integer id);

    ProductResponse upDateProduct(Integer id, ProductRequest request);

    int deleteProduct(Integer id);

    void decreaseStockProduct(Integer productId, Integer quantity);

    void increaseStockProduct(Integer productId, Integer quantity);

    List<TopSellingProductResponse> getTopSellingProducts();

    ProductPageResponse searchProduct(String keyword, int page, int size);

    ProductPageResponse findByPrice(String keyword, int page, int size);
}
