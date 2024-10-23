package com.example.csemaster.v2.domain.quiz.report;

import com.example.csemaster.core.dao.quiz.accessory.QuizReportEntity;
import com.example.csemaster.core.tools.ValidPageable;
import com.example.csemaster.v2.domain.quiz.solve.SolveService;
import com.example.csemaster.v2.dto.request.QuizReportRequest;
import com.example.csemaster.v2.dto.response.QuizReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QuizReport v2", description = "문제 신고 조회 기능")
@RestController(value = "V2ReportController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/quiz")
public class ReportController {
    private final ReportService reportService;
    private final SolveService quizSolveService;

    // 문제 신고
    @Operation(
            summary = "문제 신고 [사용자 전용]",
            description = "신고 사유를 받아서 문제를 신고"
    )
    @PostMapping("/report")
    public ResponseEntity<?> quizReport(@Validated @RequestBody QuizReportRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return quizSolveService.saveQuizReport(userId, request.getQuizId(), request.getContent());
    }

    // 전체 퀴즈 오류 신고 조회
    @Operation(
            summary = "전체 신고 조회 [관리자 전용]",
            description = "모든 문제에 대한 모든 신고를 조회할 수 있으며, 처리 여부를 필터링해서 조회할 수 있다.\n" +
                    "isProcessed=false 일때 처리 안된 신고 내역만 조회됨. [페이징 사용]"
    )
    @GetMapping("/report")
    public Page<QuizReportResponse> allQuizReport(@ValidPageable(QuizReportEntity.class) Pageable pageable, @RequestParam(required = false, defaultValue = "false") Boolean isProcessed) {
        if (isProcessed) {
            return reportService.allQuizReport(pageable);
        } else {
            return reportService.getUnProcessedQuizReport(pageable);
        }
    }

    // 특정 퀴즈의 전체 오류 신고 조회
    @Operation(
            summary = "특정 문제에 들어온 신고 조회 [관리자 전용]",
            description = "문제 ID로 해당 문제에 들어온 모든 신고들을 조회할 수 있다."
    )
    @GetMapping("/{quizId}/report")
    public Page<QuizReportResponse> getAllReportForQuiz(@PathVariable Long quizId, Pageable pageable) {
        return reportService.getAllReportForQuiz(quizId, pageable);
    }

    // 특정 오류 신고 조회
    @Operation(
            summary = "특정 신고 조회 [관리자 전용]",
            description = "신고글의 ID로 상세 내용을 조회할 수 있다. [페이징 적용]"
    )
    @GetMapping("/report/{quizReportId}")
    public QuizReportResponse getQuizReport(@PathVariable Long quizReportId) {
        return reportService.getQuizReport(quizReportId);
    }

    @Operation(
            summary = "신고 처리",
            description = "신고 내역 처리 후 완료 상태로 변경할 수 있다. (0: 미완료, 1: 처리완료)"
    )
    @PatchMapping("/report/{quizReportId}/status")
    public void patchReportStatus(@PathVariable Long quizReportId, @RequestParam Boolean status) {
        reportService.setQuizReportStatus(quizReportId, status);
    }
}
