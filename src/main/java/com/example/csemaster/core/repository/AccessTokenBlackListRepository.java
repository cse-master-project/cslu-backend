package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.token.AccessTokenBlackListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface AccessTokenBlackListRepository extends JpaRepository<AccessTokenBlackListEntity, Long> {
    Optional<AccessTokenBlackListEntity> findByAccessToken(String accessToken);

    @Query(value = "SELECT * FROM access_token_blacklist WHERE black_at <= DATE_SUB(NOW(), INTERVAL 1 HOUR)", nativeQuery = true)
    List<AccessTokenBlackListEntity> findExpiredToken();
}
