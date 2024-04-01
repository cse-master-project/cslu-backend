package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.QuizDTO;
import com.example.csemaster.dto.request.QuizImageRequest;
import com.example.csemaster.dto.request.QuizReportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizCreateController {
    private final QuizSolverService quizSolverService;
    private final QuizCreateService quizCreateService;


    @PostMapping("/report")
    public ResponseEntity<?> quizReport(@RequestBody QuizReportRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizSolverService.saveQuizReport(userId, request.getQuizId(), request.getContent());
    }

    @PostMapping("/default")
    public Long addDefaultQuiz(@RequestBody QuizDTO quizDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String managerId = authentication.getName();

        return quizCreateService.addQuizAndDefaultQuiz(quizDTO, managerId);
    }

    @PostMapping("/user")
    public Long addUserQuiz(@RequestBody QuizDTO quizDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizCreateService.addQuizAndUserQuiz(quizDTO, userId);
    }

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestBody QuizImageRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        System.out.println(id);

        // 아이디의 길이로 유저, 매니저 구분
        if (id.length() <= 20) {
            return quizCreateService.managerUploadImage(id, request.getQuizId(), request.getBase64String());
        } else {
            return quizCreateService.userUploadImage(id, request.getQuizId(), request.getBase64String());
        }
    }
}
