package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.QuizUpdateDTO;
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
public class QuizManagerService {

    private final QuizRepository quizRepository;
    private final QuizCreateService quizCreateService;

    @Transactional
    public boolean deleteQuiz(Long quizId) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);

        // 존재하는 quiz인지 확인
        if (!quiz.isPresent()) {
            throw new RuntimeException("Incorrect quizId");
        }

        quiz.get().setIsDeleted(true);
        quizRepository.save(quiz.get());

        log.info("Delete Successfully");

        return true;
    }

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
