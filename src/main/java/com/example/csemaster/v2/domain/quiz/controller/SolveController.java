package com.example.csemaster.v2.domain.quiz.controller;

import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.v2.domain.quiz.service.QuizSolverService;
import com.example.csemaster.v2.dto.request.QuizSolverRequest;
import com.example.csemaster.v2.dto.response.QuizResponse;
import com.example.csemaster.v2.utils.QuizValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Solve v2", description = "문제 풀이 기능")
@RestController(value = "V2SolveController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/quiz")
public class SolveController {
    private final QuizValidator quizValidator;
    private final QuizSolverService quizSolverService;

    // 지정한 카테고리에 맞게 무작위로 하나의 문제 제공
    @Operation(
            summary = "문제 랜덤 조회",
            description = "지정한 과목과 챕터에서 무작위로 뽑은 문제를 한 개 제공 [모바일 문제 풀이 기능 전용]"
    )
    @GetMapping("/random")
    public QuizResponse getRandomQuiz(@RequestParam String subject, @RequestParam(required = false) List<String> chapters,
                                      @RequestParam(required = false, defaultValue = "true") Boolean hasUserQuiz,
                                      @RequestParam(required = false, defaultValue = "true") Boolean hasDefaultQuiz,
                                      @RequestParam(required = false, defaultValue = "false") Boolean hasSolvedQuiz) {
        // 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 검증
        if (!quizValidator.isValidSubject(subject)) throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        if (!quizValidator.isValidChapter(subject, chapters)) throw new ApiException(ApiErrorType.NOT_FOUND_CHAPTER);

        if (!hasDefaultQuiz && !hasUserQuiz) throw new ApiException(ApiErrorType.ILLEGAL_ARGUMENT);

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuiz(userId, subject, chapters, hasUserQuiz, hasDefaultQuiz, hasSolvedQuiz);
    }

    // 여러 카테고리 문제를 무작위로 조회
    @Operation(
            summary = "과목을 여러개 고를 수 있는 문제 랜덤 조회 [모바일 문제 풀이 기능 전용]",
            description = "여러 과목들의 모든 하위 챕터에서 무작위로 하나의 문제를 제공 [모바일 문제 풀이 기능 전용]"
    )
    @GetMapping("/random/only-subject")
    public QuizResponse getRandomQuizWithSubject(@RequestParam List<String> subject,
                                                 @RequestParam(required = false, defaultValue = "true") Boolean hasUserQuiz,
                                                 @RequestParam(required = false, defaultValue = "true") Boolean hasDefaultQuiz,
                                                 @RequestParam(required = false, defaultValue = "false") Boolean hasSolvedQuiz) {
        // 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 검증
        if (!quizValidator.isValidSubject(subject)) throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);

        if(!hasDefaultQuiz && !hasUserQuiz) throw new ApiException(ApiErrorType.ILLEGAL_ARGUMENT);

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuizWithSubjects(userId, subject, hasUserQuiz, hasDefaultQuiz, hasSolvedQuiz);
    }

    // 문제 푼 결과 저장
    @Operation(
            summary = "문제 풀이 결과 저장 [모바일 문제 풀이 기능 전용]",
            description = "문제 ID와 정답 여부를 입력해 풀이 결과를 저장할 수 있다. " +
                    "문제를 풀지 않은 결과가 자동으로 저장되지 않으며, 해당 경로를 통하여 저장된 결과만 기록된다."
    )
    @PostMapping("/submit")
    public ResponseEntity<?> solveQuiz(@RequestBody QuizSolverRequest request)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizSolverService.saveQuizResult(userId, request.getQuizId(), request.getIsCorrect());
    }
}
