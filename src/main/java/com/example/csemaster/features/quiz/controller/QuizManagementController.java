package com.example.csemaster.features.quiz.controller;

import com.example.csemaster.dto.QuizRejectDTO;
import com.example.csemaster.dto.UnApprovalQuizDTO;
import com.example.csemaster.features.quiz.service.ApprovalQuizService;
import com.example.csemaster.features.quiz.service.QuizManagerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QuizManagement", description = "문제 관리 관련 기능<br> 0: 대기, 1: 승인, -1: 거절")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizManagementController {

    private final QuizManagerService quizManagerService;
    private final ApprovalQuizService approvalQuizService;

    // 미승인 문제 조회
    @Operation(
            summary = "미승인 문제 조회",
            description = "승인 상태가 미승인(0)인 문제들 전체 조회"
    )
    @GetMapping("/unapproved")
    public List<UnApprovalQuizDTO> getUnApproval() {
        return approvalQuizService.getUnApprovalQuiz();
    }

    // 0 : 대기, 1 : 승인, -1 : 거절
    // 문제 승인
    @Operation(
            summary = "문제 승인",
            description = "문제 아이디를 받아서 문제를 승인(1)으로 처리"
    )
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> userQuizApprove(@PathVariable("id") Long quizId) {
        return approvalQuizService.setQuizPermission(quizId, 1);
    }

    // 문제 반려
    @Operation(
            summary = "문제 반려",
            description = "문제 아이디를 받아서 문제를 반려(-1)로 처리 후 반려 사유 저장"
    )
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> userQuizReject(@PathVariable("id") Long quizId, @RequestBody QuizRejectDTO quizRejectDTO) {
        quizRejectDTO.setQuizId(quizId);
        return approvalQuizService.setQuizRejection(quizRejectDTO, -1);
    }

    // 문제 수정
    @Operation(
            summary = "문제 수정",
            description = "문제 아이디를 받아서 문제 내용(jsonContent)을 수정"
    )
    @PatchMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long quizId, @RequestBody String newJsonContent) {
        boolean updateJsonContent = quizManagerService.updateQuiz(quizId, newJsonContent);
        if(!updateJsonContent) {
            return ResponseEntity.badRequest().body("QuizUpdateController - updateQuiz()");
        }

        return ResponseEntity.ok().body("Update Successfully");
    }

    // 문제 삭제
    @Operation(
            summary = "문제 삭제",
            description = "문제 아이디를 받아서 is_deleted 필드에 true 처리하여 비활성화"
    )
    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
        boolean deleteQuiz = quizManagerService.deleteQuiz(quizId);

        if (!deleteQuiz) {
            return ResponseEntity.badRequest().body("QuizDeleteController - deleteQuiz()");
        }

        return ResponseEntity.ok().body("Delete Successfully");
    }
}
