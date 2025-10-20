package com.example.orderdetail_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.orderdetail_service.dtos.response.ApiResponse;
import com.example.orderdetail_service.dtos.response.ProductResponse;


@FeignClient(name = "product-service", path= "/api/v1/products")
public interface ProductClient {

    @GetMapping("/{productId}")
    ApiResponse<ProductResponse> getProductById(@PathVariable("productId") int productId);

    @PutMapping("/{productId}/stock/decrease")
    ApiResponse<String> decreaseStockProduct(@PathVariable("productId") int productId,
                       @RequestParam("quantity") int quantity);

    @PutMapping("/{productId}/stock/increase")
    ApiResponse<String> increaseStockProduct(@PathVariable("productId") int productId,
                        @RequestParam("quantity") int quantity);
}
