package com.example.csemaster.features.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizSolverService {
    private final QuizRepository quizRepository;

    @Autowired
    public QuizSolverService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public QuizResponse getQuiz(String userId, String subject, String detailSubject) {
        List<QuizEntity> quiz = quizRepository.getAnOpenQuiz(userId, subject, detailSubject);
        int randomIndex = (int)(Math.random() * quiz.size());
        return QuizMapper.INSTANCE.entityToResponse(quiz.get(randomIndex));
    }
}
