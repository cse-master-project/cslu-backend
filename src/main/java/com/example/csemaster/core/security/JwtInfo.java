package com.example.csemaster.core.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class JwtInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    private LocalDateTime refreshIseAt;
    private LocalDateTime refreshExpAt;
}
