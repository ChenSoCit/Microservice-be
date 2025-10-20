package com.example.product_service.dtos.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProductPageResponse extends PageResponse{
     private  List<ProductResponse> product;
}
