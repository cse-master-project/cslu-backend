package com.example.csemaster.features.quiz;

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
import org.springframework.data.domain.PageRequest;
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

    public List<UserQuizResponse> getUserQuiz(String userId) {
        Optional<UserEntity> user = userRepository.findById(userId);

        // userId가 존재하는지 확인
        if (!user.isPresent()) {
            throw new RuntimeException("User not found with the provided ID.");
        }

        List<UserQuizResponse> quiz = quizRepository.getUserQuiz(userId);

        return quiz;
    }

    public List<QuizRejectResponse> getQuizReject(Long quizId) {
        Optional<QuizEntity> quiz = quizRepository.findById(quizId);

        List<QuizRejectResponse> reject = quizRepository.getQuizReject(quizId);

        return reject;
    }
}
