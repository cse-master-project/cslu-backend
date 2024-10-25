package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.category.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface QuizSubjectRepository extends JpaRepository<SubjectEntity, Long> {
    public Optional<SubjectEntity> findBySubject(String subject);

    @Query("SELECT s.subject FROM SubjectEntity s ")
    List<String> getAllSubject();
}
