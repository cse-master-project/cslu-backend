package com.example.csemaster.login.user.repository;

import com.example.csemaster.login.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
