package com.example.csemaster.v2.domain.quiz.controller;

import com.example.csemaster.v2.domain.quiz.service.QuizCreateService;
import com.example.csemaster.v2.domain.quiz.service.QuizSolverService;
import com.example.csemaster.v2.dto.request.QuizImageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "QuizImage v2", description = "이미지 조회, 추가")
@RestController(value = "V2ImageController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/quiz")
public class ImageController {
    private final QuizCreateService quizCreateService;
    private final QuizSolverService quizSolverService;
    // 이미지 추가
    @Operation(
            summary = "이미지 추가",
            description = "문제 추가시 이미지는 해당 경로를 통해 별도로 전송해야한다. 요청시 필요한 문제 ID는 문제 추가 요청시 받을 수 있다."
    )
    @PostMapping("/image")
    public void uploadImage(@RequestBody QuizImageRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        // 아이디의 길이로 유저, 매니저 구분
        if (id.length() <= 20) {
            quizCreateService.managerUploadImage(request.getQuizId(), request.getBase64String());
        } else {
            quizCreateService.userUploadImage(id, request.getQuizId(), request.getBase64String());
        }
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
}
