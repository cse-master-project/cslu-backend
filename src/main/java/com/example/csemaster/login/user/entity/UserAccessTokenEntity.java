package com.example.csemaster.login.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_access_token")
public class UserAccessTokenEntity {
    @Id
    private String userId;

    @Column(name="user_access_token")
    private String accessToken;

    @Column(name="user_access_token_issued_at")
    private LocalDateTime issueAt;

    @Column(name="user_access_token_exp_at")
    private LocalDateTime expAt;
}
