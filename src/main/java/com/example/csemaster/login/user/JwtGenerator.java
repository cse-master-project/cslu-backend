package com.example.csemaster.login.user;

import com.example.csemaster.login.user.response.TokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// JWT 생성 객체
@Component
public class JwtGenerator {
    private final Key key; // HS256 알고리즘을 사용하는 SecretKey 생성

    public JwtGenerator(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    public TokenResponse generateToken(String userId) {
        long nowMillis = System.currentTimeMillis();

        String accessToken = Jwts.builder()
                .setSubject(userId) // 토큰의 주체 설정
                .setIssuedAt(new Date(nowMillis)) // 발행 시간 설정
                .setExpiration(new Date(nowMillis + 3600000)) // 1시간 뒤 만료
                .signWith(key, SignatureAlgorithm.HS256) // 서명 알고리즘과 키 설정
                .compact(); // JWT 생성

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(nowMillis + 3600000 * 12 * 7))  // 7일뒤 만료
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.of(accessToken, refreshToken);
    }
}
