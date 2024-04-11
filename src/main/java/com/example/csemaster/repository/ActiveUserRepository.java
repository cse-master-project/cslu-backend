package com.example.csemaster.repository;

import com.example.csemaster.entity.ActiveUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActiveUserRepository extends JpaRepository<ActiveUserEntity, String> {
    ActiveUserEntity findByGoogleId(String googleId);
}
