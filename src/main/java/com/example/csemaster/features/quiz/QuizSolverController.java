package com.example.csemaster.features.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/quiz")
public class QuizSolverController {
    private final QuizSolverService quizSolverService;

    @Autowired
    public QuizSolverController(QuizSolverService quizSolverService) {
        this.quizSolverService = quizSolverService;
    }

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
}
