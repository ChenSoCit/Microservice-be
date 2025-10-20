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

import com.example.product_service.dtos.response.TopSellingProductResponse;
import com.example.product_service.services.ProductService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/internal/products")
@Slf4j(topic="INTERNAL-CONTROLLER")
public class ProductInternalController {

    @Autowired
    private ProductService productService;

    @GetMapping("/statistics/top-selling")
    public ResponseEntity<List<TopSellingProductResponse>> getTopSellingProducts() {

        List<TopSellingProductResponse> result = productService.getTopSellingProducts();
    
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{productId}/stock/decrease")
    public ResponseEntity<String> updateStock(@PathVariable("productId") Integer productId
                                        , @RequestParam int quantity){
        
        productService.decreaseStockProduct(productId, quantity);

        return ResponseEntity.ok("Success");
    }
    
    @PutMapping("/{productId}/stock/increase")
    public ResponseEntity<String> increaseStockProduct(@PathVariable("productId") Integer productId
                                        , @RequestParam int quantity){
        
        productService.increaseStockProduct(productId, quantity);

        return ResponseEntity.ok("Success");
    }
}
