package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.dto.response.UserQuizResponse;
import com.example.csemaster.entity.ActiveQuizEntity;
import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.entity.UserEntity;
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
        return activeQuizRepository.findAllBy(pageable);
    }

    public Page<ActiveQuizEntity> getUserQuiz(Pageable pageable) {
        return activeQuizRepository.findAllUserQuiz(pageable);
    }

    public Page<ActiveQuizEntity> getDefaultQuiz(Pageable pageable) {
        return activeQuizRepository.findAllDefaultQuiz(pageable);
    }

    public List<UserQuizResponse> getMyQuiz(String userId) {
        Optional<UserEntity> user = userRepository.findById(userId);

        // userId가 존재하는지 확인
        if (user.isEmpty()) {
            throw new RuntimeException("User not found with the provided ID.");
        }

        return quizRepository.getUserQuiz(userId);
    }

    public List<QuizRejectResponse> getQuizReject(Long quizId) {
        return quizRepository.getQuizReject(quizId);
    }

    public Optional<QuizEntity> getQuizById(Long quizId) { return quizRepository.findByQuizId(quizId); }
}
