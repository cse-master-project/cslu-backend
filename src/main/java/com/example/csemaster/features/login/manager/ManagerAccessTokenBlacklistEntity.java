package com.example.csemaster.features.login.manager;

import com.example.csemaster.entity.ManagerEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name = "manager_access_token_blacklist")
public class ManagerAccessTokenBlacklistEntity {
    @Id
    @Column(name = "manager_id_for_a_t_blacklist")
    private String managerId;

    @OneToOne
    @JoinColumn(name = "manager_id_for_a_t_blacklist", referencedColumnName = "manager_id")
    private ManagerEntity manager;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "expiration_time")
    private Date expirationTime;
}
