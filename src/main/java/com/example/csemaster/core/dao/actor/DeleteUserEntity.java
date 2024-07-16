package com.example.csemaster.core.dao.actor;

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
    @JoinColumn(name = "user_id_for_delete", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "deleted_google_id")
    private String googleId;

    @Column(name = "deleted_nickname")
    private String nickname;

    @Column(name = "deleted_at")
    private LocalDateTime deleteAt;
}
