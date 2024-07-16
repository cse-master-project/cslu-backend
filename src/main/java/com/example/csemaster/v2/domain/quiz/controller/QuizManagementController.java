package com.example.csemaster.v2.domain.quiz.controller;

import com.example.csemaster.v2.dto.UnApprovalQuizDTO;
import com.example.csemaster.v2.domain.quiz.service.ApprovalQuizService;
import com.example.csemaster.v2.domain.quiz.service.QuizManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QuizManagement v2", description = "문제 관리 관련 기능<br> 0: 대기, 1: 승인, -1: 거절")
@RestController(value = "V2QuizManagementController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/management/quiz")
public class QuizManagementController {

    private final QuizManagerService quizManagerService;
    private final ApprovalQuizService approvalQuizService;

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

    // 문제 수정
    @Operation(
            summary = "문제 수정 [관리자 전용]",
            description = "문제의 내용(jsonContent)만 수정할 수 있다."
    )
    @PatchMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long quizId, @RequestBody String newJsonContent) {
        return quizManagerService.updateQuiz(quizId, newJsonContent);
    }

    // 문제 삭제
    @Operation(
            summary = "문제 삭제 [관리자 전용]",
            description = "문제 ID로 해당 문제를 삭제할 수 있다. (소프트 삭제로 처리됨)"
    )
    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
        return quizManagerService.deleteQuiz(quizId);
    }
}
