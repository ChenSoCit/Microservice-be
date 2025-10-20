package com.example.client_server.filter;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.client_server.client.AuthClient;
import com.example.client_server.dto.VerifyTokenResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;



@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthClient authClient;

    private static final Set<EndpointMethodPattern> BYPASS_PATTERNS = Set.of(
        new EndpointMethodPattern("/api/v1/auth/register", "POST"),
        new EndpointMethodPattern("/api/v1/auth/login", "POST"),
        new EndpointMethodPattern("/api/v1/products", "GET"),
        new EndpointMethodPattern("/api/v1/products/*", "GET") 
    );

    private static class EndpointMethodPattern {
        private final String pattern;
        private final String method;

        public EndpointMethodPattern(String pattern, String method) {
            this.pattern = pattern;
            this.method = method;
        }
    }

    private boolean isBypass(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        AntPathMatcher matcher = new AntPathMatcher();
        return BYPASS_PATTERNS.stream().anyMatch(ep ->
            matcher.match(ep.pattern, path) && method.equalsIgnoreCase(ep.method)
        );
    }

    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull FilterChain filterChain)
        throws ServletException, IOException {

    if (isBypass(request)) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = request.getHeader("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
        throw new org.springframework.security.core.AuthenticationException("Missing or invalid Authorization header") {};
    }

    token = token.substring(7);
    try {
        VerifyTokenResponse verify = authClient.verifyToken(token);
        if (verify == null || !verify.isValid()) {
            throw new org.springframework.security.core.AuthenticationException("Token is invalid") {};
        }

        request.setAttribute("X-Username", verify.getUsername());
        request.setAttribute("X-Roles", String.join(",", verify.getRoles()));

        var authorities = verify.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        var authentication = new UsernamePasswordAuthenticationToken(verify.getUsername(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
        } catch (Exception ex) {
            throw new org.springframework.security.core.AuthenticationException("Authentication failed: " + ex.getMessage()) {};
        }
    }
}
