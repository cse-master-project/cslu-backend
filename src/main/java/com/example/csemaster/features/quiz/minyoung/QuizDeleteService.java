package com.example.csemaster.features.quiz.minyoung;

import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizDeleteService {
    private final QuizRepository quizRepository;

    @Transactional
    public boolean deleteQuiz(Long quizId) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);

        // 존재하는 quiz인지 확인
        if (quiz.isEmpty()) {
            throw new RuntimeException("Incorrect quizId");
        }

        quiz.get().setIsDeleted(true);
        quizRepository.save(quiz.get());

        log.info("Delete Successfully");

        return true;
    }
}
