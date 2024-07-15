package com.example.csemaster.features.jwt;

import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import com.example.csemaster.entity.UserRefreshTokenEntity;
import com.example.csemaster.utils.TokenUtils;
import com.example.csemaster.repository.AccessTokenBlackListRepository;
import com.example.csemaster.repository.ManagerRefreshTokenRepository;
import com.example.csemaster.repository.UserRefreshTokenRepository;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;

    @Value("${ADMIN_ACCESS_TOKEN_EXPIRE_TIME}")
    private long ADMIN_ACCESS_TOKEN_EXPIRE_TIME; // 30분

    @Value("${ADMIN_REFRESH_TOKEN_EXPIRE_TIME}")
    private long ADMIN_REFRESH_TOKEN_EXPIRE_TIME; // 1시간

    @Value("${ACCESS_TOKEN_EXPIRE_TIME}")
    private long USER_ACCESS_TOKEN_EXPIRE_TIME; // 1시간 (단위: 밀리초)

    @Value("${REFRESH_TOKEN_EXPIRE_TIME}")
    private long USER_REFRESH_TOKEN_EXPIRE_TIME; // 7일

    private final ManagerRefreshTokenRepository managerRefreshTokenRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final AccessTokenBlackListRepository accessTokenBlackListRepository;

    public JwtProvider(@Value("${jwt.secret}") String key,
                       ManagerRefreshTokenRepository managerRefreshTokenRepository,
                       UserRefreshTokenRepository userRefreshTokenRepository,
                       AccessTokenBlackListRepository accessTokenBlackListRepository) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        this.managerRefreshTokenRepository = managerRefreshTokenRepository;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.accessTokenBlackListRepository = accessTokenBlackListRepository;
    }

    private LocalDateTime fromDate(Date t) {
        return t.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    // id를 받아 엑세스토큰을 생성하는 메소드
    public JwtInfo generateToken(String id) {
        Date now = new Date();
        // 권한 가져오기
        long nowMils = (new Date()).getTime();

        // id의 길이로 관리자와 사용자 구분
        if (id.length() <= 20) {
            // 관리자
            // Access Token 생성
            String accessToken = Jwts.builder()
                    .setSubject(id)
                    .claim("auth", MemberRole.ADMIN.getValue())
                    .setIssuedAt(now)
                    .setExpiration(new Date(nowMils + ADMIN_ACCESS_TOKEN_EXPIRE_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            Date refreshExp = new Date(nowMils + ADMIN_REFRESH_TOKEN_EXPIRE_TIME);
            // Refresh Token 생성
            String refreshToken = Jwts.builder()
                    .setSubject(id)
                    .setExpiration(refreshExp)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return JwtInfo.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .refreshIseAt(fromDate(now))
                    .refreshExpAt(fromDate(refreshExp))
                    .build();
        } else {
            // 사용자
            // 엑세스 토큰 발급 (만료 기간 1시간)
            String accessToken = Jwts.builder()
                    .setSubject(id)
                    .setIssuedAt(now)
                    .setExpiration(new Date(nowMils + USER_ACCESS_TOKEN_EXPIRE_TIME))
                    .claim("auth", MemberRole.USER.getValue())
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            Date refreshExp = new Date(nowMils + USER_REFRESH_TOKEN_EXPIRE_TIME);
            // 리프레시 토큰 발급 (만료 기간 7일)
            String refreshToken = Jwts.builder()
                    .setSubject(id)
                    .setExpiration(refreshExp)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return JwtInfo.builder()
                    .grantType("Bearer")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .refreshIseAt(fromDate(now))
                    .refreshExpAt(fromDate(refreshExp))
                    .build();
        }

    }

    // 액세스 토큰에서 인증정보를 추출하는 메소드
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }
        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 클레임에서 userId(managerId), 권한 추출 후 인증 객체로 반환
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 리프레시 토큰에서 인증 정보를 추출하는 메소드
    public String getIdFromRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);

        return claims.getSubject();
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        // 블랙되었는지 확인, 검색된게 없으면 유효한 토큰
        return accessTokenBlackListRepository.findByAccessToken(TokenUtils.hashString(token)).isEmpty();
    }

    // token 의 클레임 추출
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // refreshToken 검증 메서드
    public boolean validateRefreshToken(String token) {
        try {
            // user id는 36글자인데 managerId는 20자 이하이므로 길이로 유저 구분
            String id = parseClaims(token).getSubject();

            if (id.length() < 36) {
                Optional<ManagerRefreshTokenEntity> refreshToken = managerRefreshTokenRepository.findById(id);
                return refreshToken.isPresent();
            } else {
                Optional<UserRefreshTokenEntity> refreshToken = userRefreshTokenRepository.findById(id);
                return refreshToken.isPresent();
            }
        } catch (ExpiredJwtException e) {
            log.info("Expired Refresh Token");
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid Refresh Token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported Refresh Token");
        } catch (IllegalArgumentException e) {
            log.info("Refresh Token claims string is empty");
        }

        return false;
    }
}
