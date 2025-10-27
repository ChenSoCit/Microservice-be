package com.example.product_service.mappers;


import java.util.List;

import com.example.product_service.dtos.response.ProductResponse;
import com.example.product_service.models.Product;

import feign.Param;

public interface ProductMapper {
    
    int insertProduct(Product row);

    ProductResponse getProductById(int id);

    int upDateProduct(Product row);

    int deleteProduct(Integer id);

    int insertSelective(Product row);

    int decreaseStock(@Param("productId") int productId, @Param("qty") int qty);

    int increaseStock(@Param("productId") int productId, @Param("qty") int qty);

    List<ProductResponse> searchProduct(@Param("keyword") String keyword, @Param("limit") int limit, @Param("offset") int offset);

    long countByName(@Param("keyword") String keyword);

    List<ProductResponse> findAllByPrice(@Param("sort") String sort, @Param("limit") int limit, @Param("offset") int offset);

    long countAll();
}