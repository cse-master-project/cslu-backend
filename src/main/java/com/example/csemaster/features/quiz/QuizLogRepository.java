package com.example.csemaster.features.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizLogRepository extends JpaRepository<QuizLogEntity, QuizLogPK> {
    @Query("SELECT MAX(tryCnt) FROM QuizLogEntity " +
            "WHERE userId = userId AND quizId = quizId")
    Integer getMaxTryCnt(@Param("userId") String userId, @Param("userId") Long quizId);
}
