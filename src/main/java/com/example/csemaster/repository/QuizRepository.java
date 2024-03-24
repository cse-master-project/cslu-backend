package com.example.csemaster.repository;

import com.example.csemaster.entity.QuizEntity;
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
    Optional<QuizEntity> deleteByQuizId(Long quizId);
}
