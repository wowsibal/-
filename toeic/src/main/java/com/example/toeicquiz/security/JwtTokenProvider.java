package com.example.toeicquiz.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // JWT 서명 키 (32자 이상)
    private final String secret = "jwt-secret-key-jwt-secret-key-jwt-secret-key";
    private final long validityInMillis = 60 * 60 * 1000; // 유효 시간: 1시간
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    // JWT 토큰 생성 (subject에 userid 저장)
    public String createToken(String userid) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMillis);

        return Jwts.builder()
                .setSubject(userid) // 사용자 식별자 (userid)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 userid 추출
    public String getUserid(String token) {
        //JWT에서 subject를 꺼내 userid로 반환
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
