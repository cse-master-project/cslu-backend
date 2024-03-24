package com.example.csemaster.features.default_quiz;

import com.example.csemaster.features.quiz.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizEntity, String> {
    Optional<QuizEntity> findByQuizId(Long quizId);
}
