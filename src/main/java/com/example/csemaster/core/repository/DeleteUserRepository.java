package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.actor.DeleteUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface DeleteUserRepository extends JpaRepository<DeleteUserEntity, String> {
    DeleteUserEntity findByGoogleId(String googleId);
}
