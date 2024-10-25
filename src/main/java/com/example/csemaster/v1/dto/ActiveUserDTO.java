package com.example.csemaster.v1.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ActiveUserDTO {
    private String userId;
    private String googleId;
    private String nickname;
    private LocalDateTime createAt;
}
