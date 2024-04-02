package com.example.csemaster.features.login;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginUtil {
    public static String extractAccessTokenFromHeader(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // "Bearer "를 제외한 토큰 부분만 추출합니다.
        }
        return null;
    }
    public static String hashString(String input) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(input);
    }

}
