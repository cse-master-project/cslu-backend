package com.example.csemaster.v2.domain.quiz.management;

import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.core.repository.QuizRepository;
import com.example.csemaster.core.tools.QuizValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service(value = "V2ManagementService")
@Slf4j
@RequiredArgsConstructor
public class ManagementService {

    private final QuizRepository quizRepository;

    // 문제 삭제
    @Transactional
    public ResponseEntity<?> deleteQuiz(Long quizId) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);

        // 존재하는 quiz인지 확인
        if (quiz.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_ID);
        }

        quiz.get().setIsDeleted(true);
        quizRepository.save(quiz.get());

        log.info("Delete Quiz [quiz ID: " + quizId + "]");

        return ResponseEntity.ok().build();
    }

    // 문제 수정
    public ResponseEntity<?> updateQuiz(Long quizId, String newJsonContent) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);

        // 존재하는 quizId인지 확인
        if (quiz.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_ID);
        }

        // 수정 전후가 같은지 확인
        String jsonContent = quiz.get().getJsonContent();
        if (newJsonContent.equals(jsonContent)) {
            throw new ApiException(ApiErrorType.NO_CHANGE);
        }

        // 수정한 jsonContent 형식 확인
        // FIXME : 임시로 QuizVerificationService 의 변수를 사용하도록 했으니 변경 후 여기도 바꿔야함.
        QuizValidator.isValidJsonContent(quiz.get().getQuizType(), newJsonContent);

        // 수정한 jsonContent 저장
        quiz.get().setJsonContent(newJsonContent);
        quizRepository.save(quiz.get());

        log.info("Patch quiz content [quiz ID: " + quiz.get().getQuizId() + "]");

        return ResponseEntity.ok().build();
    }
}
