package com.example.csemaster;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        String requestURI = httpServletRequest.getRequestURI();
        String requestID = UUID.randomUUID().toString();
        String clientIP = request.getRemoteAddr();

        MDC.put("traceId", requestID);
        try {
            log.info("Request URI : '{}', from IP : {}", requestURI, clientIP);
            try {
                // 1. ServletInputStream 객체 얻기
                InputStream inputStream = request.getInputStream();

                // 2. 바이트 배열로 요청 바디 읽기
                byte[] buffer = new byte[4096];
                int bytesRead;
                StringBuilder requestBody = new StringBuilder();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    requestBody.append(new String(buffer, 0, bytesRead));
                }

                // 3. 바이트 배열을 문자열로 변환하여 출력
                log.debug("Request Body: " + requestBody);
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // log.info("Response [{}]", requestURI);
            MDC.clear();
        }
    }
}