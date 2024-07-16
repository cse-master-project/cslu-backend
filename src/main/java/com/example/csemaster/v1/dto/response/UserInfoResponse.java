package com.example.csemaster.v1.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoResponse {
    private String nickname;
    private LocalDateTime createAt;
}
