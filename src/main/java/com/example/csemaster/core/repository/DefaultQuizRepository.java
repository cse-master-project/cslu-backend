package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.core.DefaultQuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface DefaultQuizRepository extends JpaRepository<DefaultQuizEntity, Long> {
    @Query("SELECT q FROM DefaultQuizEntity q " +
            "WHERE q.defaultQuizId = :quizId AND q.managerId.managerId = :managerId")
    Optional<DefaultQuizEntity> findByQuizIdAndManagerId(Long quizId, String managerId);
}
