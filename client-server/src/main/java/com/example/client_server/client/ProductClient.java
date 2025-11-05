package com.example.client_server.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.client_server.dto.response.ProductPageResponse;
import com.example.client_server.dto.request.ProductRequest;
import com.example.client_server.dto.response.ProductResponse;


@FeignClient(name = "product-service",  path = "/api/v1/products")
public interface ProductClient {
    @PostMapping("/insert")
    String createProduct(@RequestBody ProductRequest request);
    
    @GetMapping("/{id}")
    ProductResponse getProduct(@PathVariable("id")long id);

    @PutMapping("/{id}")
    ProductResponse updateProduct(@PathVariable("id") long id, @RequestBody ProductRequest request);

    @PutMapping("/stock/decrease")
    String updateStockDecrease(@RequestParam int productId, @RequestParam int quantity);

    @PutMapping("/stock/increase")
    String updateStockIncrease(@RequestParam int productId, @RequestParam int quantity);

    @DeleteMapping("/{id}")
    Integer deleteProduct(@PathVariable("id") long id);

    @GetMapping("/search")
    ProductPageResponse searchProduct( @RequestParam(required = false) String keyword,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size);

    @GetMapping("/sort")                                              
    ProductPageResponse findByPrice(@RequestParam(required = false) String sort,
                                                @RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size);

}
