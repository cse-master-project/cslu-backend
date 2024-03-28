package com.example.csemaster.features.quiz.minyoung;

import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.entity.UserEntity;
import com.example.csemaster.repository.QuizRepository;
import com.example.csemaster.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewQuizService {
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
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
