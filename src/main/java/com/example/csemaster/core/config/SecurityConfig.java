package com.example.csemaster.core.config;

import com.example.csemaster.core.filter.JwtAuthenticationFilter;
import com.example.csemaster.core.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        // 계정 관리
                        .requestMatchers("/api/v2/user/auth/**").permitAll()
                        .requestMatchers("/api/v2/user/**").hasRole("USER")
                        .requestMatchers("/api/v2/manager/**").permitAll()

                        // 문제 조회
                        .requestMatchers("/api/v2/quiz/**").hasAnyRole("ADMIN")
                        .requestMatchers("/api/v2/management/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v2/quiz/**").hasRole("USER")

                        // 문제 풀이
                        .requestMatchers("/api/v2/quiz/my/**").hasAnyRole("USER")
                        .requestMatchers("/api/v2/quiz/submit").hasRole("USER")
                        .requestMatchers("/api/v2/quiz/random/**").hasRole("USER")

                        // 문제 신고
                        .requestMatchers("/api/v2/quiz/report/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v2/quiz/report").hasRole("USER")

                        // 문제 생성
                        .requestMatchers(HttpMethod.POST, "/api/v2/quiz/default").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v2/quiz/user").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/v2/quiz/image/**").hasAnyRole("USER", "ADMIN")

                        // 기타
                        .requestMatchers("/v3/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/dev/**").permitAll()
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