package com.example.product_service.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.product_service.dtos.common_dto.ProductResponse;
import com.example.product_service.dtos.request.ProductRequest;
import com.example.product_service.dtos.response.ProductPageResponse;
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
    public Integer insertProduct(@Valid @RequestBody ProductRequest request){
        log.info("Request to insert product");
        return productService.insertProduct(request);
    }

    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable("id") int id){
        log.info("Request to get product by ID: {}", id);
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public ProductResponse upDateProduct(@PathVariable("id") Integer id
                                        ,@Valid @RequestBody ProductRequest request){
        log.info("Request to update product, {}", id);
        return productService.upDateProduct(id, request);
    }

    @PutMapping("/stock/decrease")
    public String updateStockDecrease(@RequestParam int productId
                                    , @RequestParam int quantity){
        log.info("Request to update stock decrease, {}", productId);
        productService.decreaseStockProduct(productId, quantity);
        return "Decrease stock successfully";
    }

    @PutMapping("/stock/increase")
    public String increaseStockProduct(@RequestParam int productId
                                    , @RequestParam int quantity){
        log.info("Request to increase stock product, {}", productId);
        productService.increaseStockProduct(productId, quantity);
        return "Increase stock successfully";
    }

   
    @DeleteMapping("/{id}")
    public Integer deleteProduct(@PathVariable("id") Integer id) {
        log.info("Request to delete product by ID: {}", id);
        return productService.deleteProduct(id);
    }

    @GetMapping("/search")
    public ProductPageResponse searchProduct(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        log.info("Request to search product");
        return productService.searchProduct( keyword, page, size);
    }


    @GetMapping("/sort")
    public ProductPageResponse findByPrice(
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size){
        log.info("Request to sort product");
        return productService.findByPrice(sort, page, size);
    }
}
