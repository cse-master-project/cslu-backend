package com.example.csemaster.login.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "active_user")
public class ActiveUserEntity {
    @Id
    @Column(name="user_id_for_active")
    private String userId;

    @OneToOne
    @JoinColumn(name = "user_id_for_active", referencedColumnName = "user_id")
    private UserEntity user;

    @Column(name="google_id")
    private String googleId;

    @Column
    private String nickname;

    @Column(name="create_at")
    private LocalDateTime createAt;
}
