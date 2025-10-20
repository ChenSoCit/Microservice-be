package com.example.client_server.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class ProductPageResponse {
    private int pageNumber;
    private int pageSize;
    private long totalPages;
    private long totalElements;
    private  List<ProductResponse> product;
}

