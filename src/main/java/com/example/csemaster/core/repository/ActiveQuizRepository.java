package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ActiveQuizRepository extends JpaRepository<ActiveQuizEntity, Long> {
    @Query("SELECT q " +
            "FROM ActiveQuizEntity q " +
            "LEFT OUTER JOIN QuizLogEntity l ON q.quizId = l.quizId AND l.userId = :userId AND l.answerStatus = true " +
            "WHERE q.subject = :subject AND q.chapter IN :chapters AND l.quizId IS NULL")
    List<ActiveQuizEntity> getAnOpenQuiz(@Param("userId") String userId, @Param("subject") String subject, @Param("chapter") List<String> chapters);

    @Query("SELECT q " +
            "FROM ActiveQuizEntity q " +
            "LEFT OUTER JOIN QuizLogEntity l ON q.quizId = l.quizId AND l.userId = :userId AND l.answerStatus = true " +
            "WHERE q.subject = :subject AND l.quizId IS NULL")
    List<ActiveQuizEntity> getAnOpenQuiz(@Param("userId") String userId, @Param("subject") String subject);

    @Query("SELECT q " +
            "FROM ActiveQuizEntity q " +
            "WHERE q.subject = :subject AND q.chapter IN :chapters")
    List<ActiveQuizEntity> getAnOpenQuizWithSolved(@Param("subject") String subject, @Param("chapter") List<String> chapters);

    @Query("SELECT q " +
            "FROM ActiveQuizEntity q " +
            "WHERE q.subject = :subject")
    List<ActiveQuizEntity> getAnOpenQuizWithSolved(@Param("subject") String subject);

    Page<ActiveQuizEntity> findAllBy(Pageable pageable);

    @Query("SELECT q FROM ActiveQuizEntity q " +
            "WHERE q.quizId IN (SELECT u.userQuizId FROM UserQuizEntity u)")
    Page<ActiveQuizEntity> findAllUserQuiz(Pageable pageable);

    @Query("SELECT q FROM ActiveQuizEntity q " +
            "WHERE q.quizId IN (SELECT d.defaultQuizId FROM DefaultQuizEntity d)")
    Page<ActiveQuizEntity> findAllDefaultQuiz(Pageable pageable);
}
