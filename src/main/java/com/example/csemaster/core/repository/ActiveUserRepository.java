package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.actor.ActiveUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ActiveUserRepository extends JpaRepository<ActiveUserEntity, String> {
    ActiveUserEntity findByGoogleId(String googleId);
}
