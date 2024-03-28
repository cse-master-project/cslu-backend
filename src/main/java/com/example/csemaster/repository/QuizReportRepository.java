package com.example.csemaster.repository;

import com.example.csemaster.entity.QuizReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizReportRepository extends JpaRepository<QuizReportEntity, Long> {
    Optional<QuizReportEntity> findByQuizReportId(Long quizReportId);
}
