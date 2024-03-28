package com.example.csemaster.features.quiz;

import com.example.csemaster.entity.ActiveQuizEntity;
import com.example.csemaster.repository.ActiveQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizSearchService {
    private final ActiveQuizRepository activeQuizRepository;

    public Page<ActiveQuizEntity> getQuiz(PageRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return activeQuizRepository.findAllBy(pageable);
    }

    public Page<ActiveQuizEntity> getUserQuiz(PageRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return activeQuizRepository.findAllUserQuiz(pageable);
    }

    public Page<ActiveQuizEntity> getDefaultQuiz(PageRequest pageRequest) {
        Pageable pageable = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
        return activeQuizRepository.findAllDefaultQuiz(pageable);
    }
}
