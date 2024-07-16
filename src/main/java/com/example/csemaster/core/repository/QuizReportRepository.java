package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.accessory.QuizReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface QuizReportRepository extends JpaRepository<QuizReportEntity, Long> {
    Optional<QuizReportEntity> findByQuizReportId(Long quizReportId);

    @Query("SELECT r FROM QuizReportEntity r " +
            "WHERE r.quizId = :quizId")
    List<QuizReportEntity> findByQuizId(Long quizId);
}
