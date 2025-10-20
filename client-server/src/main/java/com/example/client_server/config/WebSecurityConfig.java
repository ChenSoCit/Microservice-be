package com.example.client_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.client_server.filter.AuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity 
public class WebSecurityConfig  {


    @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity http, AuthenticationFilter authenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()        
            );
        return http.build();
    }
}
