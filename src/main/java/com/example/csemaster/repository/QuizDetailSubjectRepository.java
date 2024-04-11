package com.example.csemaster.repository;

import com.example.csemaster.entity.DetailSubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizDetailSubjectRepository extends JpaRepository<DetailSubjectEntity, Long> {
    Optional<DetailSubjectEntity> findBySubjectIdAndDetailSubject(Long subjectId, String detailSubject);
}
