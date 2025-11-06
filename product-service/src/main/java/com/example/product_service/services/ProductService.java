package com.example.product_service.services;

import com.example.product_service.dtos.common_dto.ProductResponse;
import com.example.product_service.dtos.request.ProductRequest;
import com.example.product_service.dtos.response.ProductPageResponse;



public interface  ProductService {
    int insertProduct(ProductRequest request);
 
    ProductResponse getProductById(int id);

    ProductResponse upDateProduct(Integer id, ProductRequest request);

    int deleteProduct(Integer id);

    void decreaseStockProduct(int productId, int quantity);

    void increaseStockProduct(Integer productId, Integer quantity);

    ProductPageResponse searchProduct(String keyword, int page, int size);

    ProductPageResponse findByPrice(String keyword, int page, int size);
}
