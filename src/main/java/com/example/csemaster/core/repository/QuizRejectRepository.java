package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.accessory.QuizRejectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface QuizRejectRepository extends JpaRepository<QuizRejectEntity, Long> {
}
