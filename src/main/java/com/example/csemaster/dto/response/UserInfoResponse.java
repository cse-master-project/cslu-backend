package com.example.csemaster.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoResponse {
    private String nickname;
    private LocalDateTime createAt;
}
