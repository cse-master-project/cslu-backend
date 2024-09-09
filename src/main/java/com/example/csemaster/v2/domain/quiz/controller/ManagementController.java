package com.example.csemaster.v2.domain.quiz.controller;

import com.example.csemaster.v2.domain.quiz.service.ManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "QuizManagement v2", description = "문제 관리 기능")
@RestController(value = "V2ManagementController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/management/quiz")
public class ManagementController {
    private final ManagementService managementService;

    // 문제 수정
    @Operation(
            summary = "문제 수정 [관리자 전용]",
            description = "문제의 내용(jsonContent)만 수정할 수 있다."
    )
    @PatchMapping("/{quizId}")
    public ResponseEntity<?> updateQuiz(@PathVariable Long quizId, @RequestBody String newJsonContent) {
        return managementService.updateQuiz(quizId, newJsonContent);
    }

    // 문제 삭제
    @Operation(
            summary = "문제 삭제 [관리자 전용]",
            description = "문제 ID로 해당 문제를 삭제할 수 있다. (소프트 삭제로 처리됨)"
    )
    @DeleteMapping("/{quizId}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long quizId) {
        return managementService.deleteQuiz(quizId);
    }
}
