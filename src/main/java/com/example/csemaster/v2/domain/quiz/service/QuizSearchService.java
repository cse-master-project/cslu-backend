package com.example.csemaster.v2.domain.quiz.service;

import com.example.csemaster.core.repository.*;
import com.example.csemaster.v2.dto.response.QuizRejectResponse;
import com.example.csemaster.v2.dto.response.QuizResponse;
import com.example.csemaster.v2.dto.response.UserQuizResponse;
import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import com.example.csemaster.core.dao.actor.UserEntity;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.v2.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service(value = "V2QuizSearchService")
@Slf4j
@RequiredArgsConstructor
public class QuizSearchService {
    private final ActiveQuizRepository activeQuizRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final DefaultQuizRepository defaultQuizRepository;
    private final UserQuizRepository userQuizRepository;
    private final ActiveUserRepository activeUserRepository;

    public Page<ActiveQuizEntity> getQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllBy(pageable);
        } catch (Exception e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public Page<ActiveQuizEntity> getUserQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllUserQuiz(pageable);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public Page<ActiveQuizEntity> getDefaultQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllDefaultQuiz(pageable);
        } catch (ApiException e) {
            log.error(e.getMessage());
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public List<UserQuizResponse> getMyQuiz(String userId) {
        try {
            Optional<UserEntity> user = userRepository.findById(userId);

            // userId가 존재하는지 확인
            if (user.isEmpty()) {
                throw new ApiException(ApiErrorType.INVALID_IDENTIFIER);
            }

            return quizRepository.getUserQuiz(userId);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public List<QuizRejectResponse> getQuizReject(Long quizId) {
        try {
            return quizRepository.getQuizReject(quizId);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public QuizResponse getQuizById(Long quizId) {
        return QuizMapper.INSTANCE.entityToResponse(
                activeQuizRepository.findById(quizId)
                    .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND_ID))
        );
    }
}
