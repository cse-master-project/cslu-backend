package com.example.csemaster.repository;

import com.example.csemaster.entity.QuizLogEntity;
import com.example.csemaster.entity.QuizLogPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizLogRepository extends JpaRepository<QuizLogEntity, QuizLogPK> {
    @Query("SELECT max(tryCnt) FROM QuizLogEntity " +
            "WHERE userId = :userId AND quizId = :quizId")
    Integer getMaxTryCnt(String userId, Long quizId);
}
