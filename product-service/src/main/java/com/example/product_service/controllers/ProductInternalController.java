package com.example.product_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.product_service.dtos.response.ApiResponse;
import com.example.product_service.dtos.response.ProductResponse;
import com.example.product_service.dtos.response.TopSellingProductResponse;
import com.example.product_service.services.ProductService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/internal/products")
@Slf4j(topic="INTERNAL-CONTROLLER")
public class ProductInternalController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable("productId") Integer productId) {
        log.info("Internal API: Getting product by id: {}", productId);
        ProductResponse product = productService.getProductById(productId);
        
        ApiResponse<ProductResponse> response = ApiResponse.<ProductResponse>builder()
            .code(200)
            .message("Get product by id successfully")
            .data(product)
            .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics/top-selling")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSellingProducts() {

        List<TopSellingProductResponse> result = productService.getTopSellingProducts();
    
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{productId}/stock/decrease")
    public ResponseEntity<ApiResponse<String>> updateStock(@PathVariable("productId") Integer productId
                                        , @RequestParam int quantity){
        
        productService.decreaseStockProduct(productId, quantity);

        ApiResponse<String> response = ApiResponse.<String>builder()
            .code(200)
            .message("Decrease stock successfully")
            .data("Success")
            .build();

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{productId}/stock/increase")
    public ResponseEntity<ApiResponse<String>> increaseStockProduct(@PathVariable("productId") Integer productId
                                        , @RequestParam int quantity){
        
        productService.increaseStockProduct(productId, quantity);

        ApiResponse<String> response = ApiResponse.<String>builder()
            .code(200)
            .message("Increase stock successfully")
            .data("Success")
            .build();

        return ResponseEntity.ok(response);
    }
}
