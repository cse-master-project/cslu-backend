package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.core.DeleteQuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface DeleteQuizRepository extends JpaRepository<DeleteQuizEntity, Long> {
}
