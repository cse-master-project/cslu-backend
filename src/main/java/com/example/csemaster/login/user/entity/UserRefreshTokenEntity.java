package com.example.csemaster.login.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class UserRefreshTokenEntity {
    @Id
    private String userId;

    @Column(name="user_refresh_token")
    private String refreshToken;

    @Column(name="user_refresh_token_issued_at")
    private LocalDateTime issueAt;

    @Column(name="user_refresh_token_exp_at")
    private LocalDateTime expAt;
}
