package com.example.csemaster.login.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLoginRepository extends JpaRepository<UserEntity, Long> {
}
