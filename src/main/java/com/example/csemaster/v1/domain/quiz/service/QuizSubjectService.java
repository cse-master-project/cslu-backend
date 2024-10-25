package com.example.csemaster.v1.domain.quiz.service;

import com.example.csemaster.core.repository.ChapterRepository;
import com.example.csemaster.v1.dto.DetailSubjectDTO;
import com.example.csemaster.v1.dto.DetailSubjectUpdateDTO;
import com.example.csemaster.v1.dto.SubjectDTO;
import com.example.csemaster.v1.dto.response.SubjectResponse;
import com.example.csemaster.v1.dto.SubjectUpdateDTO;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.dao.quiz.category.SubjectEntity;
import com.example.csemaster.core.dao.quiz.category.ChapterEntity;
import com.example.csemaster.core.repository.QuizSubjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizSubjectService {

    private final QuizSubjectRepository quizSubjectRepository;
    private final ChapterRepository quizDetailSubjectRepository;

    public List<SubjectResponse> getAllSubject() {
        List<SubjectEntity> subjects = quizSubjectRepository.findAll();
        return subjects.stream().map(subject -> {
            SubjectResponse response = new SubjectResponse();
            response.setSubjectId(subject.getSubjectId());
            response.setSubject(subject.getSubject());
            List<String> sortedDetailSubjects = subject.getChapters().stream()
                    .sorted(Comparator.comparingInt(ChapterEntity::getSortIndex))
                    .map(ChapterEntity::getChapter)
                    .collect(Collectors.toList());
            response.setDetailSubject(sortedDetailSubjects);
            return response;
        }).collect(Collectors.toList());
    }

    public ResponseEntity<?> addSubject(SubjectDTO subjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(subjectDTO.getSubject());

        if (subjectEntity.isPresent()) {
            throw new ApiException(ApiErrorType.DUPLICATE_SUBJECT);
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
            throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        }

        Optional<ChapterEntity> existingDetailSubject = quizDetailSubjectRepository.findBySubjectIdAndChapter(
                subjectEntity.get().getSubjectId(), detailSubjectDTO.getDetailSubject());

        if (existingDetailSubject.isPresent()) {
            throw new ApiException(ApiErrorType.DUPLICATE_DETAIL_SUBJECT);
        }

        if (detailSubjectDTO.getSortIndex() == null) {
            log.debug("sortIndex가 비었음");
            throw new ApiException(ApiErrorType.NULL_VALUE);
        }

        // 해당 subject가 가진 detailSubject을 index 기준으로 오름차순 정렬
        List<ChapterEntity> detailSubjects = quizDetailSubjectRepository.findBySubjectId(subjectEntity.get().getSubjectId());
        detailSubjects.sort(Comparator.comparingInt(ChapterEntity::getSortIndex));
        
        if (detailSubjectDTO.getSortIndex() > detailSubjects.size()) {
            log.debug("sortIndex가 최대값을 초과함");
            throw new ApiException(ApiErrorType.ARGS_NOT_VALID);
        }

        // 새 detailSubject 생성
        ChapterEntity newDetailSubject = new ChapterEntity();
        newDetailSubject.setSubjectId(subjectEntity.get().getSubjectId());
        newDetailSubject.setChapter(detailSubjectDTO.getDetailSubject());
        newDetailSubject.setSortIndex(detailSubjectDTO.getSortIndex());

        // 삽입할 index 위치를 index 조정
        int insertSortIndex = findSortIndex(detailSubjects, detailSubjectDTO.getSortIndex());

        // 이후의 index들을 +1 해주기
        adjustSortIndex(detailSubjects, insertSortIndex, 1);

        // 변경된 DetailSubjectEntity 리스트 저장
        quizDetailSubjectRepository.saveAll(detailSubjects);

        // 새 DetailSubjectEntity 저장
        quizDetailSubjectRepository.save(newDetailSubject);

        log.info("Create new detail Subject( {} - {} )]", detailSubjectDTO.getSubject(), detailSubjectDTO.getDetailSubject());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> updateSubject(SubjectUpdateDTO subjectUpdateDTO) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findBySubject(subjectUpdateDTO.getSubject());

        // subject가 존재하는지 확인
        if (subject.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        }

        // 수정 전후가 같은지 확인
        String oldSubject = subject.get().getSubject();
        String newSubject = subjectUpdateDTO.getNewSubject();
        if (newSubject.equals(oldSubject)) {
            throw new ApiException(ApiErrorType.NO_CHANGE);
        }

        subject.get().setSubject(newSubject);
        quizSubjectRepository.save(subject.get());

        return ResponseEntity.ok().build();
    }

   public ResponseEntity<?> updateDetailSubject(DetailSubjectUpdateDTO updateDTO) {
        Optional<SubjectEntity> subject = quizSubjectRepository.findBySubject(updateDTO.getSubject());
        if (subject.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        }

        Optional<ChapterEntity> detailSubject = quizDetailSubjectRepository.findBySubjectIdAndChapter(subject.get().getSubjectId(), updateDTO.getDetailSubject());
        if (detailSubject.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_DETAIL_SUBJECT);
        }

        String oldDetail = detailSubject.get().getChapter();
        String newDetail = updateDTO.getNewDetailSubject();
        if (newDetail.equals(oldDetail)) {
            throw new ApiException(ApiErrorType.NO_CHANGE);
        }

        Integer sortIndex = detailSubject.get().getSortIndex();

        // detailSubject 삭제
        quizDetailSubjectRepository.delete(detailSubject.get());

        // 새로운 detailSubject 추가
        ChapterEntity newDetailSubject = new ChapterEntity();
        newDetailSubject.setSubjectId(subject.get().getSubjectId());
        newDetailSubject.setChapter(updateDTO.getNewDetailSubject());
        newDetailSubject.setSortIndex(sortIndex);

        quizDetailSubjectRepository.save(newDetailSubject);

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteSubject(SubjectDTO subjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(subjectDTO.getSubject());
        if (subjectEntity.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        }

        quizSubjectRepository.delete(subjectEntity.get());

        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deleteDetailSubject(DetailSubjectDTO detailSubjectDTO) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(detailSubjectDTO.getSubject());
        if (subjectEntity.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        }

        Optional<ChapterEntity> detail = quizDetailSubjectRepository.findBySubjectIdAndChapter(subjectEntity.get().getSubjectId(), detailSubjectDTO.getDetailSubject());
        if (detail.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_DETAIL_SUBJECT);
        }

        // 해당 subject가 가진 detailSubject을 index 기준으로 오름차순 정렬
        List<ChapterEntity> detailSubjects = quizDetailSubjectRepository.findBySubjectId(subjectEntity.get().getSubjectId());
        detailSubjects.sort(Comparator.comparingInt(ChapterEntity::getSortIndex));

        // 제거할 index 위치를 index 조정
        int insertSortIndex = findSortIndex(detailSubjects, detail.get().getSortIndex());

        // 이후의 index들을 -1 해주기
        adjustSortIndex(detailSubjects, insertSortIndex, -1);

        // 변경된 DetailSubjectEntity 리스트 저장
        quizDetailSubjectRepository.saveAll(detailSubjects);


        quizDetailSubjectRepository.delete(detail.get());

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> adjustDetailSubject(List<DetailSubjectDTO> detailSubjectDTO) {
        // 프론트로부터 받은 DTO 리스트가 DB에 존재하는지 검사
        validateDetailSubjects(detailSubjectDTO);

        // DTO를 엔티티로 변환하여 저장
        List<ChapterEntity> entities = detailSubjectDTO.stream()
                .map(dto -> {
                    ChapterEntity entity = new ChapterEntity();
                    entity.setSubjectId(getSubjectId(dto.getSubject())); // subjectId 설정
                    entity.setChapter(dto.getDetailSubject());
                    entity.setSortIndex(dto.getSortIndex());
                    return entity;
                })
                .collect(Collectors.toList());

        // 엔티티들을 저장소에 저장
        quizDetailSubjectRepository.saveAll(entities);

        // sortIndex 기준으로 오름차순 정렬
        entities.sort(Comparator.comparingInt(ChapterEntity::getSortIndex));

        // 검사: sortIndex의 순차적 증가 여부 확인
        for (int i = 0; i < entities.size(); i++) {
            if (i != entities.get(i).getSortIndex()) {
                log.debug("sortIndex가 잘못됨. index: {}, sortIndex: {}", i, entities.get(i).getSortIndex());
                throw new ApiException(ApiErrorType.ARGS_NOT_VALID);
            }
        }

        // 저장
        quizDetailSubjectRepository.saveAll(entities);

        return ResponseEntity.ok().build();
    }

    private Long getSubjectId(String subjectName) {
        Optional<SubjectEntity> subjectEntityOptional = quizSubjectRepository.findBySubject(subjectName);
        return subjectEntityOptional.map(SubjectEntity::getSubjectId).orElse(null);
    }

    public void validateDetailSubjects(List<DetailSubjectDTO> detailSubjectDTOs) {
        for (DetailSubjectDTO dto : detailSubjectDTOs) {
            // Subject가 존재하는지 확인
            Optional<SubjectEntity> subjectEntityOpt = quizSubjectRepository.findBySubject(dto.getSubject());
            if (subjectEntityOpt.isEmpty()) {
                throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
            }

            // 해당 Subject의 DetailSubject가 존재하는지 확인
            SubjectEntity subjectEntity = subjectEntityOpt.get();
            Optional<ChapterEntity> detailSubjectEntityOpt = quizDetailSubjectRepository.findBySubjectIdAndChapter(
                    subjectEntity.getSubjectId(), dto.getDetailSubject());
            if (detailSubjectEntityOpt.isEmpty()) {
                throw new ApiException(ApiErrorType.NOT_FOUND_DETAIL_SUBJECT);
            }
        }
    }

    // index 위치 찾기
    private int findSortIndex(List<ChapterEntity> detailSubjects, int sortIndex) {
        for (int i = 0; i < detailSubjects.size(); i++) {
            if (detailSubjects.get(i).getSortIndex() >= sortIndex) {
                return i;
            }
        }

        return detailSubjects.size();
    }

    // 삽입된 위치 이후의 index 변경
    private void adjustSortIndex(List<ChapterEntity> detailSubjects, int startSortIndex, int num) {
        for (int i = startSortIndex; i < detailSubjects.size(); i++) {
            detailSubjects.get(i).setSortIndex(detailSubjects.get(i).getSortIndex() + num);
        }
    }
}
