package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.core.AllQuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllQuizRepository extends JpaRepository<AllQuizEntity, Long> {
}
