package com.example.csemaster.core.repository;

import com.example.csemaster.v2.dto.QuizResultDTO;
import com.example.csemaster.core.dao.quiz.accessory.QuizLogEntity;
import com.example.csemaster.core.dao.quiz.accessory.QuizLogPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface QuizLogRepository extends JpaRepository<QuizLogEntity, QuizLogPK> {
    @Query("SELECT max(tryCnt) FROM QuizLogEntity " +
            "WHERE userId = :userId AND quizId = :quizId")
    Integer getMaxTryCnt(String userId, Long quizId);

    @Query("SELECT new com.example.csemaster.v2.dto.QuizResultDTO(l.quizId, MAX(l.tryCnt), l.quiz.subject, l.answerStatus)" +
            "FROM QuizLogEntity l " +
            "WHERE l.userId = :userId " +
            "GROUP BY l.quizId, l.quiz.subject, l.answerStatus")
    List<QuizResultDTO> findAllByUserId(String userId);

    @Query("SELECT new com.example.csemaster.v1.dto.QuizResultDTO(l.quizId, MAX(l.tryCnt), l.quiz.subject, l.answerStatus)" +
            "FROM QuizLogEntity l " +
            "WHERE l.userId = :userId " +
            "GROUP BY l.quizId, l.quiz.subject, l.answerStatus")
    List<com.example.csemaster.v1.dto.QuizResultDTO> findAllByUserIdV1(String userId);
}
