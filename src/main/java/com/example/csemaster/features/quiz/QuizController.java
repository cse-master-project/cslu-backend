package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.QuizDTO;
import com.example.csemaster.dto.request.QuizReportRequest;
import com.example.csemaster.dto.request.QuizSolverRequest;
import com.example.csemaster.dto.response.QuizResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizSolverService quizSolverService;
    private final QuizCreateService quizCreateService;

    @GetMapping("/random")
    public QuizResponse getQuiz(@RequestParam String subject, String detailSubject) {
        // 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuiz(userId, subject, detailSubject);
    }

    @PostMapping("/solver")
    public void solveQuiz(@RequestBody QuizSolverRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        quizSolverService.saveQuizResult(userId, request.getQuizId(), request.getIsCorrect());
    }

    @PostMapping("/report")
    public void quizReport(@RequestBody QuizReportRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        quizSolverService.saveQuizReport(userId, request.getQuizId(), request.getContent());
    }

    @PostMapping("/default")
    public ResponseEntity<?> addDefaultQuiz(@RequestBody QuizDTO quizDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String managerId = authentication.getName();

        Boolean defaultQuiz = quizCreateService.addQuizAndDefaultQuiz(quizDTO, managerId);
        if (!defaultQuiz) {
            return ResponseEntity.badRequest().body("DefaultQuizController - addDefaultQuiz()");
        }

        return ResponseEntity.ok().body("Quiz has been saved successfully");
    }

    @PostMapping("/user")
    public ResponseEntity<?> addUserQuiz(@RequestBody QuizDTO quizDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        Boolean userQuiz = quizCreateService.addQuizAndUserQuiz(quizDTO, userId);
        if(!userQuiz) {
            return ResponseEntity.badRequest().body("DefaultQuizController - addUserQuiz()");
        }

        return ResponseEntity.ok().body("Quiz has been saved successfully");
    }
}
