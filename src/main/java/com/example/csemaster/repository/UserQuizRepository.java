package com.example.csemaster.repository;

import com.example.csemaster.entity.UserQuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserQuizRepository extends JpaRepository<UserQuizEntity, Long> {
}
