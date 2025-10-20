package com.example.product_service.dtos.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopProductStatResponse {
    private Integer productId;
    private Long totalSold;
    
}
