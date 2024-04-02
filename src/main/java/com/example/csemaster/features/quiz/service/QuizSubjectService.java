package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.DetailSubjectDTO;
import com.example.csemaster.dto.DetailSubjectUpdateDTO;
import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.dto.SubjectUpdateDTO;
import com.example.csemaster.entity.SubjectEntity;
import com.example.csemaster.entity.DetailSubjectEntity;
import com.example.csemaster.repository.QuizDetailSubjectRepository;
import com.example.csemaster.repository.QuizSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizSubjectService {

    private final QuizSubjectRepository quizSubjectRepository;
    private final QuizDetailSubjectRepository quizDetailSubjectRepository;

    public List<SubjectResponse> getAllSubject() {
        List<SubjectEntity> subjects = quizSubjectRepository.findAll();
        return subjects.stream().map(subject -> {
            SubjectResponse response = new SubjectResponse();
            response.setSubjectId(subject.getSubjectId());
            response.setSubject(subject.getSubject());
            response.setDetailSubject(subject.getDetailSubjects().stream()
                    .map(DetailSubjectEntity::getDetailSubject)
                    .collect(Collectors.toList()));
            return response;
        }).collect(Collectors.toList());
    }

    public ResponseEntity<?> addSubject(String subject) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(subject);

        if (subjectEntity.isPresent()) {
            throw new RuntimeException("subject is already present.");
        }

        SubjectEntity newSubjectEntity = new SubjectEntity();
        newSubjectEntity.setSubjectId(null);
        newSubjectEntity.setSubject(subject);
        quizSubjectRepository.save(newSubjectEntity);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> addDetailSubject(DetailSubjectDTO detailSubjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findById(detailSubjectDTO.getSubjectId());

        if (subjectEntity.isEmpty()) {
            throw new RuntimeException("subject isn't present.");
        }

        Optional<DetailSubjectEntity> existingDetailSubject = quizDetailSubjectRepository.findBySubjectIdAndDetailSubject(
                subjectEntity.get().getSubjectId(), detailSubjectDTO.getDetailSubject());

        if (existingDetailSubject.isPresent()) {
            throw new RuntimeException("detailSubject is already present with the given subject.");
        }

        DetailSubjectEntity detailSubjectEntity = new DetailSubjectEntity();
        detailSubjectEntity.setSubjectId(subjectEntity.get().getSubjectId());
        detailSubjectEntity.setDetailSubject(detailSubjectDTO.getDetailSubject());
        quizDetailSubjectRepository.save(detailSubjectEntity);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> updateSubject(SubjectUpdateDTO subjectUpdateDTO) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findById(subjectUpdateDTO.getSubjectId());

        // subject가 존재하는지 확인
        if (subject.isEmpty()) {
            throw new RuntimeException("subject isn't present.");
        }

        // 수정 전후가 같은지 확인
        String oldSubject = subject.get().getSubject();
        String newSubject = subjectUpdateDTO.getNewSubject();
        if (newSubject.equals(oldSubject)) {
            throw new RuntimeException("Be the same before and after correction");
        }

        subject.get().setSubject(newSubject);
        quizSubjectRepository.save(subject.get());

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> updateDetailSubject(DetailSubjectUpdateDTO updateDTO) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findById(updateDTO.getSubjectId());
        if (subject.isEmpty()) {
            throw new RuntimeException("subject isn't present.");
        }

        Optional<DetailSubjectEntity> detailSubject = quizDetailSubjectRepository.findBySubjectIdAndDetailSubject(subject.get().getSubjectId(), updateDTO.getDetailSubject());
        if (detailSubject.isEmpty()) {
            throw new RuntimeException("detailSubject isn't present.");
        }

        // detailSubject 삭제
        deleteDetailSubject(subject.get().getSubjectId(), updateDTO.getDetailSubject());

        // 새로운 detailSubject 추가
        DetailSubjectDTO detailSubjectDTO = new DetailSubjectDTO();
        detailSubjectDTO.setSubjectId(updateDTO.getSubjectId());
        detailSubjectDTO.setDetailSubject(updateDTO.getNewDetailSubject());
        addDetailSubject(detailSubjectDTO);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteSubject(Long subjectId) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findById(subjectId);
        if (subject.isEmpty()) {
            throw new RuntimeException("subject isn't present.");
        }

        quizSubjectRepository.delete(subject.get());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteDetailSubject(Long subjectId, String detailSubject) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findById(subjectId);
        if (subject.isEmpty()) {
            throw new RuntimeException("subject isn't present.");
        }

        Optional<DetailSubjectEntity> detail = quizDetailSubjectRepository.findBySubjectIdAndDetailSubject(subject.get().getSubjectId(), detailSubject);
        if (detail.isEmpty()) {
            throw new RuntimeException("detailSubject isn't present.");
        }

        quizDetailSubjectRepository.delete(detail.get());

        return ResponseEntity.ok().build();
    }
}
