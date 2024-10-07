package com.example.csemaster.v2.domain.quiz.query;

import com.example.csemaster.core.dao.actor.UserEntity;
import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import com.example.csemaster.core.dao.quiz.core.AllQuizEntity;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.repository.ActiveQuizRepository;
import com.example.csemaster.core.repository.AllQuizRepository;
import com.example.csemaster.core.repository.QuizRepository;
import com.example.csemaster.core.repository.UserRepository;
import com.example.csemaster.v2.dto.response.QuizResponse;
import com.example.csemaster.v2.dto.response.UserQuizResponse;
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
public class QueryService {
    private final ActiveQuizRepository activeQuizRepository;
    private final AllQuizRepository allQuizRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    // 전체 퀴즈 조회 (페이징)
    public Page<ActiveQuizEntity> getQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllBy(pageable);
        } catch (Exception e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    // 퀴즈 아이디로 퀴즈 조회
    public QuizResponse getQuizById(Long quizId) {
        return QuizMapper.INSTANCE.entityToResponse(
                allQuizRepository.findById(quizId)
                        .orElseThrow(() -> new ApiException(ApiErrorType.NOT_FOUND_ID))
        );
    }

    // 유저 퀴즈 조회 (페이징)
    public Page<ActiveQuizEntity> getUserQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllUserQuiz(pageable);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    // 기본 퀴즈 조회 (페이징)
    public Page<ActiveQuizEntity> getDefaultQuiz(Pageable pageable) {
        try {
            return activeQuizRepository.findAllDefaultQuiz(pageable);
        } catch (ApiException e) {
            log.error(e.getMessage());
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }

    // 자신이 만든 퀴즈 조회 (페이징)
    public Page<UserQuizResponse> getMyQuiz(String userId, Pageable pageable) {
        try {
            Optional<UserEntity> user = userRepository.findById(userId);

            // userId가 존재하는지 확인
            if (user.isEmpty()) {
                throw new ApiException(ApiErrorType.INVALID_IDENTIFIER);
            }

            return quizRepository.getUserQuiz(userId, pageable);
        } catch (ApiException e) {
            throw new ApiException(ApiErrorType.RUNTIME_EXCEPTION);
        }
    }
}
