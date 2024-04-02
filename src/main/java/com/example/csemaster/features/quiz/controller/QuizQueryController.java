package com.example.csemaster.features.quiz.controller;

import com.example.csemaster.dto.request.QuizSolverRequest;
import com.example.csemaster.dto.response.*;
import com.example.csemaster.entity.ActiveQuizEntity;
import com.example.csemaster.features.quiz.service.QuizReportService;
import com.example.csemaster.features.quiz.service.QuizSearchService;
import com.example.csemaster.features.quiz.service.QuizSolverService;
import com.example.csemaster.features.quiz.service.QuizSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizQueryController {
    private final QuizSearchService quizSearchService;
    private final QuizSolverService quizSolverService;
    private final QuizSubjectService quizSubjectService;
    private final QuizReportService quizReportService;

    // 전체 카테고리 조회
    @GetMapping("/subjects")
    public List<SubjectResponse> getAllSubject() {
        return quizSubjectService.getAllSubject();
    }

    // 모든 활성화된 문제 조회
    @GetMapping("/")
    public Page<ActiveQuizEntity> getAllQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getQuiz(pageRequest);
    }

    // 현재 활성화된 유저 문제만 조회
    @GetMapping("/user")
    public Page<ActiveQuizEntity> getUserQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getUserQuiz(pageRequest);
    }

    // 현재 활성화된 기본 문제만 조회
    @GetMapping("/default")
    public Page<ActiveQuizEntity> getDefaultQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getDefaultQuiz(pageRequest);
    }

    // 지정한 카테고리에 맞게 무작위로 하나의 문제 제공
    @GetMapping("/random")
    public QuizResponse getRandomQuiz(@RequestParam String subject, @RequestParam String detailSubject) {
        // 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuiz(userId, subject, detailSubject);
    }

    // 문제 푼 결과 저장
    @PostMapping("/submit")
    public ResponseEntity<?> solveQuiz(@RequestBody QuizSolverRequest request)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizSolverService.saveQuizResult(userId, request.getQuizId(), request.getIsCorrect());
    }

    // 해당 유저가 만든 퀴즈 조회
    @GetMapping("/my")
    public List<UserQuizResponse> getMyQuiz(@RequestParam String userId) {
        return quizSearchService.getMyQuiz(userId);
    }

    // 해당 유저가 만든 퀴즈 중 거절된 퀴즈만 조회
    @GetMapping("/my/reject")
    public List<QuizRejectResponse> getQuizReject(@RequestParam Long quizId) {
        return quizSearchService.getQuizReject(quizId);
    }

    // 전체 퀴즈 오류 신고 조회
    @GetMapping("/report")
    public List<QuizReportResponse> allQuizReport() {
        return quizReportService.allQuizReport();
    }

    // 특정 퀴즈의 전체 오류 신고 조회
    @GetMapping("/{quizId}/report")
    public List<QuizReportResponse> getAllReportForQuiz(@PathVariable Long quizId) {
        return quizReportService.getAllReportForQuiz(quizId);
    }

    // 특정 오류 신고 조회
    @GetMapping("/report/{quizReportId}")
    public QuizReportResponse getQuizReport(@PathVariable Long quizReportId) {
        return quizReportService.getQuizReport(quizReportId);
    }
}
