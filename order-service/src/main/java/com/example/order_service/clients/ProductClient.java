package com.example.order_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-client", path = "/api/v1/products")
public interface ProductClient {
    @PutMapping("/stock/decrease")
    String updateStockDecrease(@RequestParam int productId, @RequestParam int quantity);
}
