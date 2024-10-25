package com.example.csemaster.core.repository;

import com.example.csemaster.v2.dto.response.QuizRejectResponse;
import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.v2.dto.response.UserQuizResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    Optional<QuizEntity> findByQuizId(Long quizId);

    // SELECT q.*, uq.user_quiz_id, uq.permission_status FROM quiz q INNER JOIN user_quiz uq ON q.quiz_id = uq.user_quiz_id WHERE uq.user_id_for_user_quiz = ? ;
    @Query("SELECT new com.example.csemaster.v2.dto.response.UserQuizResponse(q, uq) " +
            "FROM QuizEntity q JOIN UserQuizEntity uq ON q.quizId = uq.userQuizId " +
            "WHERE uq.userId.userId = :userId")
    Page<UserQuizResponse> getUserQuiz(@Param("userId") String user, Pageable pageable);

    @Query("SELECT new com.example.csemaster.v1.dto.response.UserQuizResponse(q, uq) " +
            "FROM QuizEntity q JOIN UserQuizEntity uq ON q.quizId = uq.userQuizId " +
            "WHERE uq.userId.userId = :userId")
    List<com.example.csemaster.v1.dto.response.UserQuizResponse> getUserQuizV1(@Param("userId") String user);


    @Query("SELECT new com.example.csemaster.v2.dto.response.QuizRejectResponse(q, qr) " +
            "FROM QuizEntity q JOIN QuizRejectEntity qr ON q.quizId = qr.quizId " +
            "WHERE qr.quizId = :quizId")
    List<QuizRejectResponse> getQuizReject(@Param("quizId") Long quizId);

    @Query("SELECT new com.example.csemaster.v1.dto.response.QuizRejectResponse(q, qr) " +
            "FROM QuizEntity q JOIN QuizRejectEntity qr ON q.quizId = qr.quizId " +
            "WHERE qr.quizId = :quizId")
    List<com.example.csemaster.v1.dto.response.QuizRejectResponse> getQuizRejectV1(@Param("quizId") Long quizId);
}
