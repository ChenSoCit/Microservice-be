package com.example.orderdetail_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.example.orderdetail_service.mappers")
@EnableFeignClients
public class OrderdetailServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderdetailServiceApplication.class, args);
	}

}
