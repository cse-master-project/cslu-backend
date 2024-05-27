package com.example.csemaster.jwt;

import com.example.csemaster.entity.ExceptionEntity;
import com.example.csemaster.exception.ExceptionEnum;
import com.example.csemaster.features.account.TokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
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
        } catch (SecurityException | MalformedJwtException e) {
            setErrorResponse((HttpServletResponse) response, ExceptionEnum.INVALID_JWT);
        } catch (ExpiredJwtException e) {
            setErrorResponse((HttpServletResponse) response, ExceptionEnum.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            setErrorResponse((HttpServletResponse) response, ExceptionEnum.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            setErrorResponse((HttpServletResponse) response, ExceptionEnum.ILLEGAL_ARGUMENT);
        } catch (SignatureException e) {
            setErrorResponse((HttpServletResponse) response, ExceptionEnum.SIGNATURE_EXCEPTION);
        } catch (AuthenticationException e) {
            setErrorResponse((HttpServletResponse) response, ExceptionEnum.ACCESS_DENIED_EXCEPTION);
        }
    }
    private void setErrorResponse(HttpServletResponse response, ExceptionEnum e) {
        response.setStatus(e.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ExceptionEntity exceptionEntity = ExceptionEntity.builder()
                .errorType(e.getError())
                .errorDescription(e.getDescription())
                .build();
        try {
            // Object Mapper 를 사용하여 객체를 JSON 문자열로 변환
            String json = new ObjectMapper().writeValueAsString(exceptionEntity);
            response.getWriter().write(json);
        } catch (IOException ioException) {
            logger.error("Error writing response: " + ioException.getMessage());
        }
    }
}
