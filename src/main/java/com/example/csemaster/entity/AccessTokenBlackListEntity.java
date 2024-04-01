package com.example.csemaster.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "access_token_blacklist")
public class AccessTokenBlackListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_token_id")
    private Long id;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "black_at")
    private LocalDateTime blackAt;
}
