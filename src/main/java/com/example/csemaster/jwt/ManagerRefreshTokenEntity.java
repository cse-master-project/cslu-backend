package com.example.csemaster.jwt;

import com.example.csemaster.login.manager.ManagerEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manager_refresh_token")
public class ManagerRefreshTokenEntity {
    @Id
    @Column(name = "manager_id_for_a_t")
    private String managerId;

    @OneToOne
    @JoinColumn(name = "manager_id_for_a_t", referencedColumnName = "manager_id")
    private ManagerEntity manager;

    @Column(name = "manager_refresh_token", length = 256)
    private String refreshToken;

    @Column(name = "manager_refresh_token_issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "manager_refresh_token_exp_at")
    private LocalDateTime expirationTime;

    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }
}
