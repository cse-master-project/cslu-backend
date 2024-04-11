package com.example.csemaster.repository;

import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.dto.response.UserQuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    Optional<QuizEntity> findByQuizId(Long quizId);

    // SELECT q.*, uq.user_quiz_id, uq.permission_status FROM quiz q INNER JOIN user_quiz uq ON q.quiz_id = uq.user_quiz_id WHERE uq.user_id_for_user_quiz = ? ;
    @Query("SELECT new com.example.csemaster.dto.response.UserQuizResponse(q, uq) " +
            "FROM QuizEntity q JOIN UserQuizEntity uq ON q.quizId = uq.userQuizId " +
            "WHERE uq.userId.userId = :userId")
    List<UserQuizResponse> getUserQuiz(@Param("userId") String user);

    @Query("SELECT new com.example.csemaster.dto.response.QuizRejectResponse(q, qr) " +
            "FROM QuizEntity q JOIN QuizRejectEntity qr ON q.quizId = qr.quizId " +
            "WHERE qr.quizId = :quizId")
    List<QuizRejectResponse> getQuizReject(@Param("quizId") Long quizId);
}
