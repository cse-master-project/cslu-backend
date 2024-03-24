package com.example.csemaster.repository;

import com.example.csemaster.entity.UserRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, String> {
}
