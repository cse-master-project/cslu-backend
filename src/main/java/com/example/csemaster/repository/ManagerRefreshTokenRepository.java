package com.example.csemaster.repository;

import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRefreshTokenRepository extends JpaRepository<ManagerRefreshTokenEntity, String> {
    Optional<ManagerRefreshTokenEntity> findById(String managerId);
}
