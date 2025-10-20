package com.example.order_service.dtos.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Builder
public class OrderRequest {

    private Integer userId;

    private String fullName;

    private String email;

    private String phone;

    private String address;
}
