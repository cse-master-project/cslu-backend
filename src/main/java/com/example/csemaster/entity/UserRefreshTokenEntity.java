package com.example.csemaster.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "user_refresh_token")
public class UserRefreshTokenEntity {
    @Id
    @Column(name="user_id_for_r_t")
    private String userId;

    @OneToOne
    @JoinColumn(name = "user_id_for_r_t", referencedColumnName = "user_id")
    private UserEntity user;

    @Column(name="user_refresh_token")
    private String refreshToken;

    @Column(name="user_refresh_token_issued_at")
    private LocalDateTime issueAt;

    @Column(name="user_refresh_token_exp_at")
    private LocalDateTime expAt;
}
