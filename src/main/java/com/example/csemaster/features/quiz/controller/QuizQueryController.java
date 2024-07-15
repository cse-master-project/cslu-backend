package com.example.csemaster.features.quiz.controller;

import com.example.csemaster.dto.UnApprovalQuizDTO;
import com.example.csemaster.dto.request.QuizSolverRequest;
import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.dto.response.QuizReportResponse;
import com.example.csemaster.dto.response.QuizResponse;
import com.example.csemaster.dto.response.UserQuizResponse;
import com.example.csemaster.entity.ActiveQuizEntity;
import com.example.csemaster.exception.CustomException;
import com.example.csemaster.exception.ExceptionEnum;
import com.example.csemaster.utils.QuizValidator;
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
    private final QuizValidator quizValidator;

    // 모든 활성화된 문제 조회
    @Operation(
            summary = "문제 전체 조회 [관리자 전용]",
            description = "활성화 상태인 문제 전체를 조회 (활성화 기준: 승인된 유저 문제나 삭제되지 않은 문제) [페이징 적용]"
    )
    @GetMapping("")
    public Page<ActiveQuizEntity> getAllQuiz(Pageable pageable) {
        return quizSearchService.getQuiz(pageable);
    }

    // 현재 활성화된 유저 문제만 조회
    @Operation(
            summary = "사용자 문제 전체 조회",
            description = "활성화 상태인 사용자 문제 전체를 조회 [페이징 적용]"
    )
    @GetMapping("/user")
    public Page<ActiveQuizEntity> getUserQuiz(Pageable pageable) {
        return quizSearchService.getUserQuiz(pageable);
    }

    // 현재 활성화된 기본 문제만 조회
    @Operation(
            summary = "기본 문제 전체 조회",
            description = "활성화 상태인 기본 문제 전체를 조회 [페이징 적용]"
    )
    @GetMapping("/default")
    public Page<ActiveQuizEntity> getDefaultQuiz(Pageable pageable) {
        return quizSearchService.getDefaultQuiz(pageable);
    }

    // 문제 아이디로 문제 조회
    @Operation(
            summary = "문제 ID로 원하는 문제 하나를 조회한다. (예: /quiz/2 -> 2번 문제 조회)"
    )
    @GetMapping("/{quizId}")
    public UnApprovalQuizDTO getQuizById(@PathVariable Long quizId) { return quizSearchService.getQuizById(quizId); }

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
        if (!quizValidator.isValidSubject(subject)) throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        if (!quizValidator.isValidDetailSubject(subject, chapters)) throw new CustomException(ExceptionEnum.NOT_FOUND_CHAPTER);

        if (!hasDefaultQuiz && !hasUserQuiz) throw new CustomException(ExceptionEnum.ILLEGAL_ARGUMENT);

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
        if (!quizValidator.isValidSubject(subject)) throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);

        if(!hasDefaultQuiz && !hasUserQuiz) throw new CustomException(ExceptionEnum.ILLEGAL_ARGUMENT);

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuizWithSubjects(userId, subject, hasUserQuiz, hasDefaultQuiz, hasSolvedQuiz);
    }

    // 문제 이미지 조회
    @Operation(
            summary = "문제 이미지 조회",
            description = "문제 ID로 해당 문제의 이미지를 조회 (base64 인코딩 사용)"
    )
    @GetMapping("/{quizId}/image")
    public ResponseEntity<?> getQuizImage(@PathVariable("quizId") Long quizId) {
        // 문제가 없는 경우 예외 처리 안함
        return quizSolverService.getQuizImage(quizId);
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

    // 해당 유저가 만든 퀴즈 조회
    @Operation(
            summary = "자신이 추가한 문제만 조회 [사용자 전용]",
            description = "로그인된 유저가 만든 문제를 조회한다."
    )
    @GetMapping("/my")
    public List<UserQuizResponse> getMyQuiz() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizSearchService.getMyQuiz(userId);
    }

    // 문제의 승인 여부 확인
    @Operation(
            summary = "사용자 문제 승인 여부 조회 [사용자 전용]",
            description = "자신이 만든 문제의 승인 여부를 quiz id를 통해 확인할 수 있다."
    )
    @GetMapping("/my/reject")
    public List<QuizRejectResponse> getQuizReject(@RequestParam Long quizId) {
        return quizSearchService.getQuizReject(quizId);
    }

    // 전체 퀴즈 오류 신고 조회
    @Operation(
            summary = "전체 신고 조회 [관리자 전용]",
            description = "모든 문제에 대한 모든 신고를 조회할 수 있다."
    )
    @GetMapping("/report")
    public List<QuizReportResponse> allQuizReport() {
        return quizReportService.allQuizReport();
    }

    // 특정 퀴즈의 전체 오류 신고 조회
    @Operation(
            summary = "특정 문제에 들어온 신고 조회 [관리자 전용]",
            description = "문제 ID로 해당 문제에 들어온 모든 신고들을 조회할 수 있다."
    )
    @GetMapping("/{quizId}/report")
    public List<QuizReportResponse> getAllReportForQuiz(@PathVariable Long quizId) {
        return quizReportService.getAllReportForQuiz(quizId);
    }

    // 특정 오류 신고 조회
    @Operation(
            summary = "특정 신고 조회 [관리자 전용]",
            description = "신고글의 ID로 상세 내용을 조회할 수 있다."
    )
    @GetMapping("/report/{quizReportId}")
    public QuizReportResponse getQuizReport(@PathVariable Long quizReportId) {
        return quizReportService.getQuizReport(quizReportId);
    }
}
