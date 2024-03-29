package com.example.csemaster.repository;

import com.example.csemaster.features.login.manager.ManagerAccessTokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ManagerAccessTokenBlacklistRepository extends JpaRepository<ManagerAccessTokenBlacklistEntity, String> {
    void deleteByExpirationTimeBefore(Date now);
}
