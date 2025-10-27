package com.example.product_service.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.product_service.dtos.request.ProductRequest;
import com.example.product_service.dtos.response.ApiResponse;
import com.example.product_service.dtos.response.ProductPageResponse;
import com.example.product_service.dtos.response.ProductResponse;
import com.example.product_service.services.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j(topic = "PRODUCT_CONTROLLER")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    

    @PostMapping("/insert")
    public ResponseEntity<String> insertProduct(@Valid @RequestBody ProductRequest request){
        log.info("Request to insert product");
        
        productService.insertProduct(request);
        return ResponseEntity.ok("Create product successfully");
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") int id) {
        log.info("Request to get product by ID: {}", id);

        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> upDateProduct(@PathVariable("id") Integer id, @RequestBody ProductRequest request) {
        log.info("Request to update product");

        ProductResponse product = productService.upDateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/stock/decrease")
    public ResponseEntity<String> updateStockDecrease(@RequestParam int productId
            , @RequestParam int quantity){

        productService.decreaseStockProduct(productId, quantity);

        return ResponseEntity.ok("Decrease stock successfully");
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteProduct(@PathVariable("id") Integer id) {

    int deletedCount = productService.deleteProduct(id);
    return ResponseEntity.ok(deletedCount);
    }

    @GetMapping("/search")
    public ResponseEntity<ProductPageResponse> searchProduct(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){

        ProductPageResponse pageResponse = productService.searchProduct( keyword, page, size);
        return ResponseEntity.ok(pageResponse);
    }


    @GetMapping("/sort")
    public ResponseEntity<ProductPageResponse> findByPrice(
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        ProductPageResponse pageResponse = productService.findByPrice(sort, page, size);
        return ResponseEntity.ok(pageResponse);
    }
         
}
