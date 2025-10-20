package com.example.auth_service.components;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access-token.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenExpiration;
    
    public String generateAccessToken(Map<String, Object> claims, String subject){

        try {
            return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(getSignKey(), SignatureAlgorithm.HS256) // hs256 chi encode khong decode dc 
            .compact();
        } catch (Exception e) {
            log.error("can not jwt token: {}",e.getMessage());
            return null;
        }
    }

    public String generateRefreshToken(String subject){
        try {
            return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis()+refreshTokenExpiration))
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact();
        } catch (Exception e) {
            log.error("Cannot generate refresh token: {}", e.getMessage());
            return null;
        }
    }

    public String getUsernameFromToken(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Cannot extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token){
        Date expirationDate = this.extractClaims(token, Claims::getExpiration);
        return expirationDate.before(new java.util.Date());
    }

    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public boolean validateToken(String token){
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Invalid token:{}", e.getMessage());
            return false;
        }
    }

    private Key getSignKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // decode base64 ra chuoi that
        return Keys.hmacShaKeyFor(keyBytes); // tạo Key chuẩn cho HMAC-SHA256.
    }

    public Long getAccessTokenExpiration(){
        return accessTokenExpiration;
    }
    // Thêm: trả về ngày giờ hết hạn của access token hiện tại (java.util.Date)
    public Date getAccessTokenExpiryDate() {
        return new Date(System.currentTimeMillis() + accessTokenExpiration);
    }

    // Thêm: trích xuất ngày hết hạn trực tiếp từ token
    public Date getExpirationDateFromToken(String token) {
        try {
            return extractClaims(token, Claims::getExpiration);
        } catch (Exception e) {
            log.error("Cannot extract expiration from token: {}", e.getMessage());
            return null;
        }
    }
}
