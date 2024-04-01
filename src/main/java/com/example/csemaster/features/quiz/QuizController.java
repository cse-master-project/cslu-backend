package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.QuizDTO;
import com.example.csemaster.dto.request.QuizImageRequest;
import com.example.csemaster.dto.request.QuizReportRequest;
import com.example.csemaster.dto.request.QuizSolverRequest;
import com.example.csemaster.dto.response.QuizResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizController {
    private final QuizSolverService quizSolverService;
    private final QuizCreateService quizCreateService;


    @GetMapping("/random")
    public QuizResponse getRandomQuiz(@RequestParam String subject, @RequestParam String detailSubject) {
        // 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuiz(userId, subject, detailSubject);
    }

    @PostMapping("/solver")
    public ResponseEntity<?> solveQuiz(@RequestBody QuizSolverRequest request)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizSolverService.saveQuizResult(userId, request.getQuizId(), request.getIsCorrect());
    }

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
