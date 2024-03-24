package com.example.csemaster.features.quiz;

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

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> quizApprove(@PathVariable("id") Long quizId) {
        return approvalQuizService.setQuizPermission(quizId);
    }
}
