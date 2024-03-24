package com.example.csemaster.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;

    @Value("${THIRTY_MINUTES}")
    private long ADMIN_ACCESS_TOKEN_EXPIRE_TIME; // 30분

    @Value("${ONE_HOUR}")
    private long ADMIN_REFRESH_TOKEN_EXPIRE_TIME; // 1시간

    @Value("${ACCESS_TOKEN_EXPIRE_TIME}")
    private long USER_ACCESS_TOKEN_EXPIRE_TIME; // 1시간 (단위: 밀리초)

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long USER_REFRESH_TOKEN_EXPIRE_TIME; // 7일

    public JwtProvider(@Value("${jwt.secret}") String key) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
    }

    // manager 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public JwtInfo generateToken(Authentication authentication) {

        long now = (new Date()).getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", MemberRole.ADMIN.getValue())
                .setExpiration(new Date(now + ADMIN_ACCESS_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + ADMIN_REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 유저의 userId를 받아 엑세스토큰을 생성하는 메소드
    public JwtInfo generateToken(String userId) {
        // 권한 가져오기
        long now = (new Date()).getTime();

        // 엑세스 토큰 발급 (만료 기간 1시간)
        String accessToken = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + USER_ACCESS_TOKEN_EXPIRE_TIME))
                .claim("auth", MemberRole.USER.getValue())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        System.out.println(MemberRole.USER);
        // 리프레시 토큰 발급 (만료 기간 7일)
        String refreshToken = Jwts.builder()
                .setSubject(userId)
                .setExpiration(new Date(now + USER_REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return JwtInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 토큰에서 인증정보를 추출하는 메소드
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        System.out.println(claims.get("auth"));
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 클레임에서 userId(managerId), 권한 추출 후 인증 객체로 반환
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty");
        }
        return false;
    }

    // accessToken의 클레임 추출
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // refreshToken 검증 메서드
    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired Refresh Token", e);
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid Refresh Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported Refresh Token", e);
        } catch (IllegalArgumentException e) {
            log.info("Refresh Token claims string is empty", e);
        }

        return false;
    }
}
