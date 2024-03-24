package com.example.csemaster.login.user.repository;

import com.example.csemaster.login.user.entity.UserRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, String> {
}
