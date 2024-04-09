package com.example.csemaster.features.quiz.controller;

import com.example.csemaster.dto.request.QuizSolverRequest;
import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.dto.response.QuizReportResponse;
import com.example.csemaster.dto.response.QuizResponse;
import com.example.csemaster.dto.response.UserQuizResponse;
import com.example.csemaster.entity.ActiveQuizEntity;
import com.example.csemaster.features.quiz.service.QuizReportService;
import com.example.csemaster.features.quiz.service.QuizSearchService;
import com.example.csemaster.features.quiz.service.QuizSolverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QuizQuery", description = "문제 조회 관련 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizQueryController {
    private final QuizSearchService quizSearchService;
    private final QuizSolverService quizSolverService;
    private final QuizReportService quizReportService;

    // 모든 활성화된 문제 조회
    @Operation(
            summary = "문제 전체 조회",
            description = "활성화 상태인 문제 전체를 조회"
    )
    @GetMapping("")
    public Page<ActiveQuizEntity> getAllQuiz(Pageable pageable) {
        return quizSearchService.getQuiz(pageable);
    }

    // 현재 활성화된 유저 문제만 조회
    @Operation(
            summary = "사용자 문제 전체 조회",
            description = "활성화 상태인 사용자 문제 전체를 조회"
    )
    @GetMapping("/user")
    public Page<ActiveQuizEntity> getUserQuiz(Pageable pageable) {
        return quizSearchService.getUserQuiz(pageable);
    }

    // 현재 활성화된 기본 문제만 조회
    @Operation(
            summary = "기본 문제 전체 조회",
            description = "활성화 상태인 기본 문제 전체를 조회"
    )
    @GetMapping("/default")
    public Page<ActiveQuizEntity> getDefaultQuiz(Pageable pageable) {
        return quizSearchService.getDefaultQuiz(pageable);
    }

    // 지정한 카테고리에 맞게 무작위로 하나의 문제 제공
    @Operation(
            summary = "문제 랜덤 조회",
            description = "카테고리를 받아서 지정한 카테고리에 맞게 무작위로 하나의 문제 제공"
    )
    @GetMapping("/random")
    public QuizResponse getRandomQuiz(@RequestParam String subject, @RequestParam String detailSubject) {
        // 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuiz(userId, subject, detailSubject);
    }

    // 문제 이미지 조회
    @Operation(
            summary = "문제 이미지 조회",
            description = "문제 아이디를 받아서 해당하는 문제의 이미지를 조회"
    )
    @GetMapping("/{quizId}/image")
    public ResponseEntity<?> getQuizImage(@PathVariable Long quizId) {
        return quizSolverService.getQuizImage(quizId);
    }

    // 문제 푼 결과 저장
    @Operation(
            summary = "문제 풀이 결과 저장",
            description = "문제 아이디와 정답 여부를 받아서 풀이 결과를 저장"
    )
    @PostMapping("/submit")
    public ResponseEntity<?> solveQuiz(@RequestBody QuizSolverRequest request)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizSolverService.saveQuizResult(userId, request.getQuizId(), request.getIsCorrect());
    }

    // 해당 유저가 만든 퀴즈 조회
    @Operation(
            summary = "특정 사용자가 추가한 문제만 조회",
            description = "사용자 아이디를 받아서 해당 사용자가 추가한 문제만 조회"
    )
    @GetMapping("/my")
    public List<UserQuizResponse> getMyQuiz(@RequestParam String userId) {
        return quizSearchService.getMyQuiz(userId);
    }

    // 문제의 승인 여부 확인
    @Operation(
            summary = "사용자 문제의 승인 여부 조회"
    )
    @GetMapping("/my/reject")
    public List<QuizRejectResponse> getQuizReject(@RequestParam Long quizId) {
        return quizSearchService.getQuizReject(quizId);
    }

    // 전체 퀴즈 오류 신고 조회
    @Operation(
            summary = "신고가 들어온 문제 전체를 조회"
    )
    @GetMapping("/report")
    public List<QuizReportResponse> allQuizReport() {
        return quizReportService.allQuizReport();
    }

    // 특정 퀴즈의 전체 오류 신고 조회
    @Operation(
            summary = "특정 문제에 들어온 신고 조회",
            description = "문제 아이디를 받아서 해당 문제에 들어온 신고들을 조회"
    )
    @GetMapping("/{quizId}/report")
    public List<QuizReportResponse> getAllReportForQuiz(@PathVariable Long quizId) {
        return quizReportService.getAllReportForQuiz(quizId);
    }

    // 특정 오류 신고 조회
    @Operation(
            summary = "특정 신고를 조회",
            description = "문제 신고 아이디를 받아서 해당하는 신고를 조회"
    )
    @GetMapping("/report/{quizReportId}")
    public QuizReportResponse getQuizReport(@PathVariable Long quizReportId) {
        return quizReportService.getQuizReport(quizReportId);
    }
}
