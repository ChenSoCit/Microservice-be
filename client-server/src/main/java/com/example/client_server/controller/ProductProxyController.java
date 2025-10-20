package com.example.client_server.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.client_server.client.ProductCient;
import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.ProductPageResponse;
import com.example.client_server.dto.ProductRequest;
import com.example.client_server.dto.ProductResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductProxyController {
    private final ProductCient productClient;

    @PostMapping("/insert")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<String> insertProduct(@RequestBody ProductRequest request){
        return productClient.createProduct(request);
    }

    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getProduct(@PathVariable("id")long id){
        return productClient.getProduct(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> updateProduct(@PathVariable("id") long id, @RequestBody ProductRequest request){
        return productClient.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Integer> deleteProduct(@PathVariable("id") long id){
        return productClient.deleteProduct(id);
    }

    @GetMapping("/search")
    ProductPageResponse searchProduct( @RequestParam(required = false) String keyword,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size){
        return productClient.searchProduct(keyword, page, size);
    }

    @GetMapping("/sort")                                             
    ProductPageResponse findByPrice(@RequestParam(required = false) String sort,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size){
        return productClient.findByPrice(sort, page, size);
    }
}
