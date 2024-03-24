package com.example.csemaster.features.quiz.minyoung;

import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.features.quiz.QuizCreateService;
import com.example.csemaster.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizUpdateService {
    private final QuizRepository quizRepository;
    private final QuizCreateService quizCreateService;
    public boolean updateQuiz(QuizUpdateDTO quizUpdateDTO) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizUpdateDTO.getQuizId());

        // 존재하는 quizId인지 확인
        if (!quiz.isPresent()) {
            throw new RuntimeException("Incorrect quizId");
        }

        // 수정 전후가 같은지 확인
        String jsonContent = quiz.get().getJsonContent();
        String newJsonContent = quizUpdateDTO.getNewJsonContent();
        if (newJsonContent.equals(jsonContent)) {
            throw new RuntimeException("Be the same before and after correction");
        }

        // 수정한 jsonContent 형식 확인
        Boolean checkNewJsonContent = quizCreateService.isValidJsonContent(newJsonContent);
        if (!checkNewJsonContent) {
            throw new RuntimeException("Incorrect jsonContent");
        }

        // 수정한 jsonContent 저장
        quiz.get().setJsonContent(newJsonContent);
        quizRepository.save(quiz.get());

        log.info("jsonContent 수정 완료");

        return true;
    }
}
