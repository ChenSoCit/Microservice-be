package com.example.product_service.dtos.response;

import java.util.List;

import com.example.product_service.dtos.common_dto.ProductResponse;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductPageResponse extends PageResponse{
     private  List<ProductResponse> product;
}
