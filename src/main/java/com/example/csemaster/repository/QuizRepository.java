package com.example.csemaster.repository;

import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.entity.UserQuizEntity;
import com.example.csemaster.features.quiz.minyoung.UserQuizResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {
    //SELECT Q.* FROM quiz Q LEFT OUTER JOIN (SELECT * FROM quiz_log WHERE user_id_for_quiz_log ="123") L ON Q.QUIZ_ID = L.QUIZ_ID_FOR_QUIZ_LOG where Q.subject = "a" AND Q.detail_subject = "b" ;
    @Query("SELECT q " +
            "FROM QuizEntity q " +
            "LEFT OUTER JOIN QuizLogEntity l ON q.quizId = l.quizId AND l.userId = :userId " +
            "WHERE q.subject = :subject AND q.detailSubject = :detailSubject")
    List<QuizEntity> getAnOpenQuiz(@Param("userId") String userId, @Param("subject") String subject, @Param("detailSubject") String detailSubject);

    Optional<QuizEntity> findByQuizId(Long quizId);



    // SELECT q.*, uq.user_quiz_id, uq.permission_status FROM quiz q INNER JOIN user_quiz uq ON q.quiz_id = uq.user_quiz_id WHERE uq.user_id_for_user_quiz = ? ;
    @Query("SELECT new com.example.csemaster.features.quiz.minyoung.UserQuizResponse(q, uq) " +
            "FROM QuizEntity q JOIN UserQuizEntity uq ON q.quizId = uq.userQuizId " +
            "WHERE uq.userId.userId = :userId")
    List<UserQuizResponse> getUserQuiz(@Param("userId") String user);
}
