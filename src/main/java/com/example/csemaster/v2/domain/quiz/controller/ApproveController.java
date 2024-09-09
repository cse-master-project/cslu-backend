package com.example.csemaster.v2.domain.quiz.controller;

import com.example.csemaster.v2.domain.quiz.service.ApprovalQuizService;
import com.example.csemaster.v2.domain.quiz.service.QuizSearchService;
import com.example.csemaster.v2.dto.UnApprovalQuizDTO;
import com.example.csemaster.v2.dto.response.QuizRejectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QuizApprove v2", description = "문제 승인 기능<br> 0: 대기, 1: 승인, -1: 거절")
@RestController(value = "V2QuizApproveController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/management/quiz")
public class ApproveController {
    private final ApprovalQuizService approvalQuizService;
    private final QuizSearchService quizSearchService;

    // 미승인 문제 조회
    @Operation(
            summary = "사용자 문제 중 미승인 문제 조회 [관리자 전용]",
            description = "사용자 문제 중 대기중(0)인 문제 전체를 조회할 수 있다."
    )
    @GetMapping("/unapproved")
    public List<UnApprovalQuizDTO> getUnApproval() {
        return approvalQuizService.getUnApprovalQuiz();
    }

    // 0 : 대기, 1 : 승인, -1 : 거절
    // 문제 승인
    @Operation(
            summary = "사용자 문제 승인 [관리자 전용]",
            description = "사용자 문제 중 대기 중인 문제를 승인(1)할 수 있다."
    )
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> userQuizApprove(@PathVariable("id") Long quizId) {
        return approvalQuizService.setQuizPermission(quizId, 1);
    }

    // 문제 반려
    @Operation(
            summary = "사용자 문제 반려 [관리자 전용]",
            description = "사용자 문제 중 대기 중인 문제를 반려(-1)할 수 있으며 사유를 지정해야한다."
    )
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> userQuizReject(@PathVariable("id") Long quizId, @RequestBody String reason) {
        return approvalQuizService.setQuizRejection(quizId, reason, -1);
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
}
