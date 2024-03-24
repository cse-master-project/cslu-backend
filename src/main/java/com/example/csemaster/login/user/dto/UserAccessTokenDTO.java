package com.example.csemaster.login.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserAccessTokenDTO {
    private String userId;
    private String token;
    private LocalDateTime issueAt;
    private LocalDateTime expAt;
}
