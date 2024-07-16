package com.example.csemaster.core.filter;

import com.example.csemaster.core.exception.ErrorResponse;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.tools.TokenUtils;
import com.example.csemaster.core.security.JwtProvider;
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
            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            setErrorResponse((HttpServletResponse) response, ApiErrorType.INVALID_JWT);
        } catch (ExpiredJwtException e) {
            setErrorResponse((HttpServletResponse) response, ApiErrorType.EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            setErrorResponse((HttpServletResponse) response, ApiErrorType.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            setErrorResponse((HttpServletResponse) response, ApiErrorType.ILLEGAL_ARGUMENT);
        } catch (SignatureException e) {
            setErrorResponse((HttpServletResponse) response, ApiErrorType.SIGNATURE_EXCEPTION);
        } catch (AuthenticationException e) {
            setErrorResponse((HttpServletResponse) response, ApiErrorType.ACCESS_DENIED_EXCEPTION);
        }
    }
    private void setErrorResponse(HttpServletResponse response, ApiErrorType e) {
        response.setStatus(e.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse exceptionModel = ErrorResponse.builder()
                .errorType(e.getError())
                .errorDescription(e.getDescription())
                .build();
        try {
            // Object Mapper 를 사용하여 객체를 JSON 문자열로 변환
            String json = new ObjectMapper().writeValueAsString(exceptionModel);
            response.getWriter().write(json);
        } catch (IOException ioException) {
            logger.error("Error writing response: " + ioException.getMessage());
        }
    }
}
