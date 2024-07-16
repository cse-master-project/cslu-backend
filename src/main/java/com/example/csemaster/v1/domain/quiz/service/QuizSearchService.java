package com.example.csemaster.v1.domain.quiz.service;

import com.example.csemaster.v1.dto.UnApprovalQuizDTO;
import com.example.csemaster.v1.dto.response.QuizRejectResponse;
import com.example.csemaster.v1.dto.response.QuizResponse;
import com.example.csemaster.v1.dto.response.UserQuizResponse;
import com.example.csemaster.core.dao.actor.ActiveUserEntity;
import com.example.csemaster.core.dao.actor.UserEntity;
import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import com.example.csemaster.core.dao.quiz.core.DefaultQuizEntity;
import com.example.csemaster.core.dao.quiz.core.UserQuizEntity;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.repository.*;
import com.example.csemaster.v1.mapper.QuizMapper;
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
    private final DefaultQuizRepository defaultQuizRepository;
    private final UserQuizRepository userQuizRepository;
    private final ActiveUserRepository activeUserRepository;

    public Page<QuizResponse> getQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllBy(pageable).map(QuizMapper.INSTANCE::entityToResponse);
        } catch (Exception e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public Page<QuizResponse> getUserQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllUserQuiz(pageable).map(QuizMapper.INSTANCE::entityToResponse);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public Page<QuizResponse> getDefaultQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllDefaultQuiz(pageable).map(QuizMapper.INSTANCE::entityToResponse);
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

            return quizRepository.getUserQuizV1(userId);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public List<QuizRejectResponse> getQuizReject(Long quizId) {
        try {
            return quizRepository.getQuizRejectV1(quizId);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    public UnApprovalQuizDTO getQuizById(Long quizId) {
        try {
            // 기본 문제인 경우
            Optional<DefaultQuizEntity> defaultQuiz = defaultQuizRepository.findById(quizId);
            if (defaultQuiz.isPresent()) {
                return new UnApprovalQuizDTO(defaultQuiz.get().getQuiz(), "관리자");
            }

            // 사용자 문제인 경우
            Optional<UserQuizEntity> userQuiz = userQuizRepository.findById(quizId);
            if (userQuiz.isPresent()) {
                UserEntity user = userQuiz.get().getUserId();
                
                // 비활성화된 사용자인 경우
                if (!user.getIsActive()) {
                    return new UnApprovalQuizDTO(userQuiz.get().getQuiz(), "탈퇴한 사용자");
                }

                // 활성화된 사용자인 경우
                Optional<ActiveUserEntity> userNickname = activeUserRepository.findById(user.getUserId());
                return new UnApprovalQuizDTO(userQuiz.get().getQuiz(), userNickname.get().getNickname());
            }

            throw new ApiException(ApiErrorType.NOT_FOUND_ID);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }
}
