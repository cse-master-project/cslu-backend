package com.example.csemaster.repository;

import com.example.csemaster.entity.AccessTokenBlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessTokenBlackListRepository extends JpaRepository<AccessTokenBlackListEntity, Long> {
    AccessTokenBlackListEntity findByAccessToken(String accessToken);
}
