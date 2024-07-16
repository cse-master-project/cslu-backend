package com.example.csemaster.v1.dto.request;

import lombok.Data;

@Data
public class SignUpRequest {
    private String accessToken;
    private String nickname;
}
