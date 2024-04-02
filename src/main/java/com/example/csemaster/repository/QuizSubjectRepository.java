package com.example.csemaster.repository;

import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuizSubjectRepository extends JpaRepository<SubjectEntity, Long> {
    public Optional<SubjectEntity> findBySubject(String subject);
}
