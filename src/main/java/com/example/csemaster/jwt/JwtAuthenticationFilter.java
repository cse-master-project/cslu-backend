package com.example.csemaster.jwt;

import com.example.csemaster.features.account.TokenUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 1. Request Header에서 토큰 추출
        String token = TokenUtils.extractAccessTokenFromHeader((HttpServletRequest) request);
        // 2. 토큰 유효성 검사
        if (token != null && jwtProvider.validateToken(token)) {
            // 스프링 시큐리티 컨텍스트에 유저의 인증정보 제공
            Authentication authentication = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        String clientIp = request.getRemoteAddr();
        logger.info("Request from IP: " + clientIp);
        chain.doFilter(request, response);
    }
}
