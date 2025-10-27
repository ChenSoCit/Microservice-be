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
import com.example.client_server.client.ProductClient;
import com.example.client_server.dto.ApiResponse;
import com.example.client_server.dto.response.ProductPageResponse;
import com.example.client_server.dto.request.ProductRequest;
import com.example.client_server.dto.response.ProductResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductProxyController {
    private final ProductClient productClient;

    @PostMapping("/insert")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponse<String> insertProduct(@RequestBody ProductRequest request){
        String response = productClient.createProduct(request);

        return ApiResponse.<String>builder()
                .code(200)
                .data(response)
                .message("Product created successfully")
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<ProductResponse> getProduct(@PathVariable("id")long id){
        ProductResponse productResponse = productClient.getProduct(id);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .data(productResponse)
                .message("Product fetched successfully")
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<ProductResponse> updateProduct(@PathVariable("id") long id, @RequestBody ProductRequest request){
        ProductResponse productResponse = productClient.updateProduct(id, request);
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .data(productResponse)
                .message("Product updated successfully")
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<Integer> deleteProduct(@PathVariable("id") long id){
        Integer response = productClient.deleteProduct(id);
        return ApiResponse.<Integer>builder()
                .code(200)
                .data(response)
                .message("Product deleted successfully")
                .build();
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
