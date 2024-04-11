package com.example.csemaster;

import com.example.csemaster.jwt.JwtAuthenticationFilter;
import com.example.csemaster.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // HTTP 기본 인증, CSRF 보안, 세션 비활성화
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 엔드포인트별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/auth/google/login").permitAll()
                        .requestMatchers("/api/user/auth/google/sign-up").permitAll()
                        .requestMatchers("/api/user/auth/refresh").permitAll()
                        .requestMatchers("/api/manager/login").permitAll()
                        .requestMatchers("/api/manager/refresh").permitAll()

                        .requestMatchers("/api/manager/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/api/quiz/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/management/**").hasRole("ADMIN")
                        .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
                        .anyRequest().denyAll())

                // 인증과정에서 JWT 검증을 수행하는 기본 필터와 사용자 정의 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt Encoder 사용
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}