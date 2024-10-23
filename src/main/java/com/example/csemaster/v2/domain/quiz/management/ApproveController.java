package com.example.csemaster.v2.domain.quiz.management;

import com.example.csemaster.v2.domain.quiz.query.QueryService;
import com.example.csemaster.v2.dto.UnApprovalQuizDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Quiz Approve", description = "문제 승인 기능<br> 0: 대기, 1: 승인, -1: 거절")
@RestController(value = "V2ApproveController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/quiz")
public class ApproveController {
    private final ApproveService approveService;
    private final QueryService queryService;

    // 미승인 문제 조회
    @Operation(
            summary = "사용자 문제 중 미승인 문제 조회 [관리자 전용]",
            description = "사용자 문제 중 대기중(0)인 문제 전체를 조회할 수 있다. [페이징 적용]"
    )
    @GetMapping("/unapproved")
    public Page<UnApprovalQuizDTO> getUnApproval(Pageable pageable) {
        return approveService.getUnApprovalQuiz(pageable);
    }

    // 0 : 대기, 1 : 승인, -1 : 거절
    // 문제 승인
    @Operation(
            summary = "사용자 문제 승인 [관리자 전용]",
            description = "사용자 문제 중 대기 중인 문제를 승인(1)할 수 있다."
    )
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> userQuizApprove(@PathVariable("id") Long quizId) {
        return approveService.setQuizPermission(quizId, 1);
    }

    // 문제 반려
    @Operation(
            summary = "사용자 문제 반려 [관리자 전용]",
            description = "사용자 문제 중 대기 중인 문제를 반려(-1)할 수 있으며 사유를 지정해야한다."
    )
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> userQuizReject(@PathVariable("id") Long quizId, @RequestBody String reason) {
        return approveService.setQuizRejection(quizId, reason, -1);
    }


}
