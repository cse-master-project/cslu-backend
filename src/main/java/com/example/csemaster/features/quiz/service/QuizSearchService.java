package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.UnApprovalQuizDTO;
import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.dto.response.UserQuizResponse;
import com.example.csemaster.entity.*;
import com.example.csemaster.exception.CustomException;
import com.example.csemaster.exception.ExceptionEnum;
import com.example.csemaster.repository.*;
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
            log.error(e.getMessage());
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

            throw new CustomException(ExceptionEnum.NOT_FOUND_ID);
        } catch (CustomException e) {
            throw new CustomException(ExceptionEnum.RUNTIME_EXCEPTION);
        }
    }
}
