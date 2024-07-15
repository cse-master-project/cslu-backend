package com.example.csemaster.utils;

import com.example.csemaster.entity.ChapterEntity;
import com.example.csemaster.entity.SubjectEntity;
import com.example.csemaster.repository.QuizSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizValidator {
    private final QuizSubjectRepository quizSubjectRepository;

    public SubjectEntity getSubjectEntity(String subject) {
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(subject);
        return subjectEntity.orElse(null);
    }

    // subject 유효성 검사
    public boolean isValidSubject(String subject) {
        return getSubjectEntity(subject) != null;
    }

    public boolean isValidSubject(List<String> subject) {
        return ListUtils.compareList(quizSubjectRepository.getAllSubject(), subject);
    }

    // detail subject 유효성 검사
    public boolean isValidDetailSubject(String subject, List<String> chapter) {
        try {
            // 요청받은 subject 의 detailSubject 검색
            List<ChapterEntity> bass = getSubjectEntity(subject).getChapters();
            // db 의 내용과 요청한 내용과 동일한지 비교
            return ListUtils.compareList(bass.stream().map(ChapterEntity::getChapter).toList(), chapter);
        } catch (Exception e) {
            // 비교중 null 이 반환돼서 예외가 발생함. 이는 유효한 값이 아니라는 것을 나타냄
            return false;
        }
    }
}
