package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.token.ManagerRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ManagerRefreshTokenRepository extends JpaRepository<ManagerRefreshTokenEntity, String> {
}
