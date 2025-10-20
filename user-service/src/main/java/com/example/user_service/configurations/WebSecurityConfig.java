package com.example.user_service.configurations;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http
        // Vô hiệu hóa CSRF vì chúng ta dùng token-based authentication (stateless)
        .csrf(AbstractHttpConfigurer::disable)

        // // Cấu hình session management là STATELESS
        // // Vì mỗi request đều phải chứa thông tin xác thực, không dựa vào session phía server
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
        );
        return http.build();
    }
}
