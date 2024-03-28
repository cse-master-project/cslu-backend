package com.example.csemaster.features.login.user;

import com.example.csemaster.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_access_token_blacklist")
public class UserAccessTokenBlacklistEntity {
    @Id
    @Column(name = "user_id_for_a_t_blacklist")
    private String userId;

    @OneToOne
    @JoinColumn(name = "user_id_for_a_t_blacklist", referencedColumnName = "user_id")
    private UserEntity user;

    @Column(name = "access_token")
    private String accessToken;
}
