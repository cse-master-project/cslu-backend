package com.example.csemaster.v2.domain.quiz.controller;

import com.example.csemaster.v2.dto.QuizDTO;
import com.example.csemaster.v2.dto.request.QuizImageRequest;
import com.example.csemaster.v2.dto.request.QuizReportRequest;
import com.example.csemaster.v2.domain.quiz.service.QuizCreateService;
import com.example.csemaster.v2.domain.quiz.service.QuizSolverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "QuizCreate v2", description = "문제 생성 관련 기능")
@RestController(value = "V2QuizCreateController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/quiz")
@Slf4j
public class QuizCreateController {
    private final QuizSolverService quizSolverService;
    private final QuizCreateService quizCreateService;

    // 문제 신고
    @Operation(
            summary = "문제 신고 [사용자 전용]",
            description = "신고 사유를 받아서 문제를 신고"
    )
    @PostMapping("/report")
    public ResponseEntity<?> quizReport(@Validated @RequestBody QuizReportRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return quizSolverService.saveQuizReport(userId, request.getQuizId(), request.getContent());
    }

    // 기본 문제 추가
    @Operation(
            summary = "기본 문제 추가 [관리자 전용]",
            description = "과목, 챕터, 문제를 받아서 문제를 추가<br>관리자 아이디를 추출하여 기본 문제로 분류"
    )
    @PostMapping("/default")
    public Long addDefaultQuiz(@RequestBody @Valid QuizDTO quizDTO) {
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();

        return quizCreateService.addQuizAndDefaultQuiz(quizDTO, managerId);
    }

    // 사용자 문제 추가
    @Operation(
            summary = "사용자 문제 추가 [사용자 전용]",
            description = "과목, 챕터, 문제를 받아서 문제를 추가<br>사용자 아이디를 추출하여 사용자 문제로 분류"
    )
    @PostMapping("/user")
    public Long addUserQuiz(@RequestBody @Valid QuizDTO quizDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizCreateService.addQuizAndUserQuiz(quizDTO, userId);
    }

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
}
