package com.example.product_service.mapstruct;

import org.mapstruct.Mapper;

import com.example.product_service.dtos.request.ProductRequest;
import com.example.product_service.models.Product;

@Mapper(componentModel = "spring")
public interface ProductMapStruct{
    Product toProduct(ProductRequest request);
}
