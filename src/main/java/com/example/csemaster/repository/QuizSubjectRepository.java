package com.example.csemaster.repository;

import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.entity.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuizSubjectRepository extends JpaRepository<SubjectEntity, Long> {
}
