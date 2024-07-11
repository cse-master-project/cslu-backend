package com.example.csemaster.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);
        String requestURI = httpServletRequest.getRequestURI();
        String requestID = UUID.randomUUID().toString();
        String clientIP = request.getRemoteAddr();

        // traceId 저장
        MDC.put("traceId", requestID);
        try {
            chain.doFilter(httpServletRequest, httpServletResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("Request URI : {}, from IP : {}", requestURI, clientIP);

            // request body 출력
            String reqContent = new String(httpServletRequest.getContentAsByteArray());
            log.debug("Request Body : {}" , reqContent);

            // response body 출력
            String resContent = new String(httpServletResponse.getContentAsByteArray());
            int httpStatus = httpServletResponse.getStatus();
            log.debug("State : {}, Response Body : {}" , httpStatus, resContent);
            // 로깅시 읽은 response body 복제
            httpServletResponse.copyBodyToResponse();

            // traceId 삭제
            MDC.clear();
        }
    }
}