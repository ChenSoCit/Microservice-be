package com.example.product_service.dtos.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse{
    private int pageNumber;
    private int pageSize;
    private long totalPages;
    private long totalElements;

}
