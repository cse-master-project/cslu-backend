package com.example.csemaster.repository;

import com.example.csemaster.dto.QuizResultDTO;
import com.example.csemaster.entity.QuizLogEntity;
import com.example.csemaster.entity.QuizLogPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizLogRepository extends JpaRepository<QuizLogEntity, QuizLogPK> {
    @Query("SELECT max(tryCnt) FROM QuizLogEntity " +
            "WHERE userId = :userId AND quizId = :quizId")
    Integer getMaxTryCnt(String userId, Long quizId);

    @Query("SELECT new com.example.csemaster.dto.QuizResultDTO(l.quizId, MAX(l.tryCnt), l.quiz.subject, l.answerStatus)" +
            "FROM QuizLogEntity l " +
            "WHERE l.userId = :userId " +
            "GROUP BY l.quizId, l.quiz.subject, l.answerStatus")
    List<QuizResultDTO> findAllByUserId(String userId);
}
