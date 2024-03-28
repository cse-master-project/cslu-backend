package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.QuizRejectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class ApprovalQuizController {
    private final ApprovalQuizService approvalQuizService;
    @GetMapping("/unapproved")
    public List<UnApprovalQuizDTO> getUnApproval() {
        return approvalQuizService.getUnApprovalQuiz();
    }

    // 0 : 대기, 1 : 승인, -1 : 거절
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> userQuizApprove(@PathVariable("id") Long quizId) {
        return approvalQuizService.setQuizPermission(quizId, 1);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> userQuizReject(@PathVariable("id") Long quizId, @RequestBody QuizRejectDTO quizRejectDTO) {
        quizRejectDTO.setQuizId(quizId);
        return approvalQuizService.setQuizRejection(quizRejectDTO, -1);
    }
}
