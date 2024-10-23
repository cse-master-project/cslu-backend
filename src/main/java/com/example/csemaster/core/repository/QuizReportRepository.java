package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.accessory.QuizReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizReportRepository extends JpaRepository<QuizReportEntity, Long> {
    Optional<QuizReportEntity> findByQuizReportId(Long quizReportId);

    @Query("SELECT r FROM QuizReportEntity r " +
            "WHERE r.quizId = :quizId")
    Page<QuizReportEntity> findByQuizId(Long quizId, Pageable pageable);

    // v1
    @Query("SELECT r FROM QuizReportEntity r " +
            "WHERE r.quizId = :quizId")
    List<QuizReportEntity> findByQuizId_v1(Long quizId);

    @Query("SELECT r FROM QuizReportEntity r " +
            "WHERE r.isProcessed = false")
    Page<QuizReportEntity> findUnprocessed(Pageable pageable);
}
