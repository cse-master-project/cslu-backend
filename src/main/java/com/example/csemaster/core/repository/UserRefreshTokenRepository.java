package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.token.UserRefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshTokenEntity, String> {
}
