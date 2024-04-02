package com.example.csemaster.repository;

import com.example.csemaster.entity.AccessTokenBlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccessTokenBlackListRepository extends JpaRepository<AccessTokenBlackListEntity, Long> {
    Optional<AccessTokenBlackListEntity> findByAccessToken(String accessToken);
}
