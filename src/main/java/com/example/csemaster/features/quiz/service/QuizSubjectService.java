package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.DetailSubjectDTO;
import com.example.csemaster.dto.DetailSubjectUpdateDTO;
import com.example.csemaster.dto.SubjectDTO;
import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.dto.SubjectUpdateDTO;
import com.example.csemaster.exception.CustomException;
import com.example.csemaster.exception.ExceptionEnum;
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

    public ResponseEntity<?> addSubject(SubjectDTO subjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(subjectDTO.getSubject());

        if (subjectEntity.isPresent()) {
            throw new CustomException(ExceptionEnum.DUPLICATE_SUBJECT);
        }

        SubjectEntity newSubjectEntity = new SubjectEntity();
        newSubjectEntity.setSubjectId(null);
        newSubjectEntity.setSubject(subjectDTO.getSubject());
        quizSubjectRepository.save(newSubjectEntity);

        log.info("Create new subject( {} )]", subjectDTO.getSubject());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> addDetailSubject(DetailSubjectDTO detailSubjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(detailSubjectDTO.getSubject());

        if (subjectEntity.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        }

        Optional<DetailSubjectEntity> existingDetailSubject = quizDetailSubjectRepository.findBySubjectIdAndDetailSubject(
                subjectEntity.get().getSubjectId(), detailSubjectDTO.getDetailSubject());

        if (existingDetailSubject.isPresent()) {
            throw new CustomException(ExceptionEnum.DUPLICATE_DETAIL_SUBJECT);
        }

        DetailSubjectEntity detailSubjectEntity = new DetailSubjectEntity();
        detailSubjectEntity.setSubjectId(subjectEntity.get().getSubjectId());
        detailSubjectEntity.setDetailSubject(detailSubjectDTO.getDetailSubject());
        quizDetailSubjectRepository.save(detailSubjectEntity);

        log.info("Create new detail Subject( {} - {} )]", detailSubjectDTO.getSubject(), detailSubjectDTO.getDetailSubject());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> updateSubject(SubjectUpdateDTO subjectUpdateDTO) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findBySubject(subjectUpdateDTO.getSubject());

        // subject가 존재하는지 확인
        if (subject.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        }

        // 수정 전후가 같은지 확인
        String oldSubject = subject.get().getSubject();
        String newSubject = subjectUpdateDTO.getNewSubject();
        if (newSubject.equals(oldSubject)) {
            throw new CustomException(ExceptionEnum.NO_CHANGE);
        }

        subject.get().setSubject(newSubject);
        quizSubjectRepository.save(subject.get());

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> updateDetailSubject(DetailSubjectUpdateDTO updateDTO) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findBySubject(updateDTO.getSubject());
        if (subject.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        }

        Optional<DetailSubjectEntity> detailSubject = quizDetailSubjectRepository.findBySubjectIdAndDetailSubject(subject.get().getSubjectId(), updateDTO.getDetailSubject());
        if (detailSubject.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_DETAIL_SUBJECT);
        }

        String oldDetail = detailSubject.get().getDetailSubject();
        String newDetail = updateDTO.getNewDetailSubject();
        if (newDetail.equals(oldDetail)) {
            throw new CustomException(ExceptionEnum.NO_CHANGE);
        }

        // detailSubject 삭제
        DetailSubjectDTO detailSubjectDTO = new DetailSubjectDTO();
        detailSubjectDTO.setSubject(updateDTO.getSubject());
        detailSubjectDTO.setDetailSubject(updateDTO.getDetailSubject());
        deleteDetailSubject(detailSubjectDTO);

        // 새로운 detailSubject 추가
        DetailSubjectDTO newDetailSubjectDTO = new DetailSubjectDTO();
        newDetailSubjectDTO.setSubject(updateDTO.getSubject());
        newDetailSubjectDTO.setDetailSubject(updateDTO.getNewDetailSubject());
        addDetailSubject(newDetailSubjectDTO);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteSubject(SubjectDTO subjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(subjectDTO.getSubject());
        if (subjectEntity.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        }

        quizSubjectRepository.delete(subjectEntity.get());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteDetailSubject(DetailSubjectDTO detailSubjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(detailSubjectDTO.getSubject());
        if (subjectEntity.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        }

        Optional<DetailSubjectEntity> detail = quizDetailSubjectRepository.findBySubjectIdAndDetailSubject(subjectEntity.get().getSubjectId(), detailSubjectDTO.getDetailSubject());
        if (detail.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_DETAIL_SUBJECT);
        }

        quizDetailSubjectRepository.delete(detail.get());

        return ResponseEntity.ok().build();
    }
}
