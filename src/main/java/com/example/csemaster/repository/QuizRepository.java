package com.example.csemaster.repository;

import com.example.csemaster.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    Optional<QuizEntity> findByQuizId(Long quizId);
}
