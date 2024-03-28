package com.example.csemaster.repository;

import com.example.csemaster.entity.DefaultQuizEntity;
import com.example.csemaster.entity.QuizRejectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRejectRepository extends JpaRepository<QuizRejectEntity, Long> {
}
