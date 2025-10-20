package com.example.product_service.clients;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.product_service.dtos.response.ApiResponse;
import com.example.product_service.dtos.response.TopProductStatResponse;


@FeignClient(name = "orderdetail-service", path = "/api/internal/order-detail")
public interface  OrderDetailClient {
    
    @GetMapping("/statistics/top-products")
    ApiResponse<List<TopProductStatResponse>> getTopProduct();
}
