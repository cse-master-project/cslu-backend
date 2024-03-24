package com.example.csemaster.features.quiz;

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
        if (!quiz.isPresent()) {
            throw new RuntimeException("Incorrect jsonContent");
        }

        quizRepository.deleteByQuizId(quizId);

        log.info("Delete Successfully");

        return true;
    }
}
