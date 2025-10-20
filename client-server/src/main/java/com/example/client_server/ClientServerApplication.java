package com.example.client_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ClientServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientServerApplication.class, args);
	}

}
