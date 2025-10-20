package com.example.order_service.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Integer id;

    private Integer userId;

    private String fullName;

    private String email;

    private String phone;

    private String address;

    private LocalDateTime orderDate;

    private String status;

    private BigDecimal totalMoney;


}
