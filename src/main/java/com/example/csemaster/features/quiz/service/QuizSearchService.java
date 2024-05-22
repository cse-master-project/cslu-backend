package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.dto.response.UserQuizResponse;
import com.example.csemaster.entity.*;
import com.example.csemaster.repository.ActiveQuizRepository;
import com.example.csemaster.repository.QuizRepository;
import com.example.csemaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizSearchService {
    private final ActiveQuizRepository activeQuizRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    public Page<ActiveQuizEntity> getQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllBy(pageable);
        } catch (Exception e) {
            throw new CustomException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
    }

    public Page<ActiveQuizEntity> getUserQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllUserQuiz(pageable);
        } catch (CustomException e) {
            throw new CustomException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
    }

    public Page<ActiveQuizEntity> getDefaultQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllDefaultQuiz(pageable);
        } catch (CustomException e) {
            throw new CustomException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
    }

    public List<UserQuizResponse> getMyQuiz(String userId) {
        try {
            Optional<UserEntity> user = userRepository.findById(userId);

            // userId가 존재하는지 확인
            if (user.isEmpty()) {
                throw new CustomException(ExceptionEnum.INVALID_IDENTIFIER);
            }

            return quizRepository.getUserQuiz(userId);
        } catch (CustomException e) {
            throw new CustomException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
    }

    public List<QuizRejectResponse> getQuizReject(Long quizId) {
        try {
            return quizRepository.getQuizReject(quizId);
        } catch (CustomException e) {
            throw new CustomException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
    }

    public Optional<QuizEntity> getQuizById(Long quizId) {
        try {
            return quizRepository.findByQuizId(quizId);
        } catch (CustomException e) {
            throw new CustomException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
    }
}
