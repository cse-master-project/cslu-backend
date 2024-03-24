package com.example.csemaster.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ManagerJwtInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
