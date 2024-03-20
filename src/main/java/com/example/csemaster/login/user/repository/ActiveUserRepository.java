package com.example.csemaster.login.user.repository;

import com.example.csemaster.login.user.entity.ActiveUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveUserRepository extends JpaRepository<ActiveUserEntity, String> {
    ActiveUserEntity findByGoogleId(String googleId);
}
