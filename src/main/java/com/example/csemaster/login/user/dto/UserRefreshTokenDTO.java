package com.example.csemaster.login.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserRefreshTokenDTO {
    private String userId;
    private String refreshToken;
    private LocalDateTime issueAt;
    private LocalDateTime expAt;
}
