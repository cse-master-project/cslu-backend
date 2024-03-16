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
@Table(name = "active_user")
public class ActiveUserEntity {
    @Id
    private String userId;

    @Column(name="google_id")
    private String googleId;

    @Column
    private String nickname;

    @Column(name="created_at")
    private LocalDateTime createAt;
}
