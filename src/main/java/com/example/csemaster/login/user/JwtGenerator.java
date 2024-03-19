package com.example.csemaster.login.user;

import com.example.csemaster.login.user.response.TokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

// JWT 생성 객체
@Component
public class JwtGenerator {
    private final Key key; // HS256 알고리즘을 사용하는 SecretKey 생성
    private static long ACCESS_TOKEN_EXPIRE_TIME; // 1시간 (단위: 밀리초)
    private static long REFRESH_TOKEN_EXPIRE_TIME; // 7일

    @Value("${ACCESS_TOKEN_EXPIRE_TIME}")
    public void setAccessTokenExpireTime(long expireTime) {
        ACCESS_TOKEN_EXPIRE_TIME = expireTime;
    }
    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    public void setRefreshTokenExpireTime(long expireTime) {
        REFRESH_TOKEN_EXPIRE_TIME = expireTime;
    }

    public JwtGenerator(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }
    public TokenResponse generateToken(String userId) {
        Instant now = Instant.now();
        Instant accessTokenExpiration = now.plusMillis(ACCESS_TOKEN_EXPIRE_TIME); // 액세스 토큰 만료 시간
        Instant refreshTokenExpiration = now.plusMillis(REFRESH_TOKEN_EXPIRE_TIME); // 리프레시 토큰 만료 시간

        String accessToken = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(accessTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setExpiration(Date.from(refreshTokenExpiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.of(
                accessToken,
                refreshToken,
                LocalDateTime.ofInstant(now, ZoneId.systemDefault()),
                LocalDateTime.ofInstant(refreshTokenExpiration, ZoneId.systemDefault())
        );
    }
}
