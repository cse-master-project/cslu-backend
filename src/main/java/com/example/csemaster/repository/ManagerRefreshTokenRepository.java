package com.example.csemaster.repository;

import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagerRefreshTokenRepository extends JpaRepository<ManagerRefreshTokenEntity, String> {
}
