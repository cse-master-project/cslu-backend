package com.example.csemaster.features.quiz.service;

import com.example.csemaster.exception.CustomException;
import com.example.csemaster.exception.ExceptionEnum;
import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> deleteQuiz(Long quizId) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);

        // 존재하는 quiz인지 확인
        if (quiz.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_ID);
        }

        quiz.get().setIsDeleted(true);
        quizRepository.save(quiz.get());

        log.info("문제 삭제 성공 [quiz ID: " + quiz.get().getQuizId() + "]");

        return ResponseEntity.ok().body("Delete Successfully");
    }

    public ResponseEntity<?> updateQuiz(Long quizId, String newJsonContent) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);

        // 존재하는 quizId인지 확인
        if (quiz.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_ID);
        }

        // 수정 전후가 같은지 확인
        String jsonContent = quiz.get().getJsonContent();
        if (newJsonContent.equals(jsonContent)) {
            throw new CustomException(ExceptionEnum.NO_CHANGE);
        }

        // 수정한 jsonContent 형식 확인
        boolean checkNewJsonContent = quizCreateService.isValidJsonContent(quiz.get().getQuizType(), newJsonContent);
        if (!checkNewJsonContent) {
            throw new CustomException(ExceptionEnum.INCORRECT_QUIZ_CONTENT);
        }

        // 수정한 jsonContent 저장
        quiz.get().setJsonContent(newJsonContent);
        quizRepository.save(quiz.get());

        log.info("jsonContent 수정 완료 [quiz ID: " + quiz.get().getQuizId() + "]");

        return ResponseEntity.ok().body("Update Successfully");
    }
}
