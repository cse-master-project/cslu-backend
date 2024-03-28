package com.example.csemaster.repository;

import com.example.csemaster.features.login.user.UserAccessTokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccessTokenBlacklistRepository extends JpaRepository<UserAccessTokenBlacklistEntity, String> {
}
