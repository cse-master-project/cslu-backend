package com.example.csemaster.features.quiz.controller;

import com.example.csemaster.dto.QuizRejectDTO;
import com.example.csemaster.dto.UnApprovalQuizDTO;
import com.example.csemaster.features.quiz.service.ApprovalQuizService;
import com.example.csemaster.features.quiz.service.QuizManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizManagementController {

    private final QuizManagerService quizManagerService;
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

    @PatchMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long quizId, @RequestBody String newJsonContent) {
        boolean updateJsonContent = quizManagerService.updateQuiz(quizId, newJsonContent);
        if(!updateJsonContent) {
            return ResponseEntity.badRequest().body("QuizUpdateController - updateQuiz()");
        }

        return ResponseEntity.ok().body("Update Successfully");
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
        boolean deleteQuiz = quizManagerService.deleteQuiz(quizId);

        if (!deleteQuiz) {
            return ResponseEntity.badRequest().body("QuizDeleteController - deleteQuiz()");
        }

        return ResponseEntity.ok().body("Delete Successfully");
    }
}
