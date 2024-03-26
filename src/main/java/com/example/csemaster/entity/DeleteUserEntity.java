package com.example.csemaster.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "delete_user")
public class DeleteUserEntity {
    @Id
    @Column(name = "user_id_for_delete")
    private String userId;

    @OneToOne
    @JoinColumn(name = "user_id_for_delete", referencedColumnName = "user_id")
    private UserEntity user;

    @Column(name = "delete_google_id")
    private String googleId;

    @Column(name = "delete_nickname")
    private String nickname;

    @Column(name = "delete_at")
    private LocalDateTime deleteAt;
}
