package com.example.csemaster;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

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
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);
        String requestURI = httpServletRequest.getRequestURI();
        String requestID = UUID.randomUUID().toString();
        String clientIP = request.getRemoteAddr();

        MDC.put("traceId", requestID);
        try {
            log.info("Request URI : '{}', from IP : {}", requestURI, clientIP);

            chain.doFilter(httpServletRequest, response);

            String reqContent = new String(httpServletRequest.getContentAsByteArray());
            log.debug("Request Body : '{}'" , reqContent);

            String resContent = new String(httpServletResponse.getContentAsByteArray());
            log.debug("Response Body : '{}'" , resContent);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // log.info("Response [{}]", requestURI);
            MDC.clear();
        }
    }
}