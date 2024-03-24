package com.example.csemaster.features.default_quiz;

import com.example.csemaster.features.quiz.QuizDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/default-quiz")
public class DefaultQuizController {
    private final DefaultQuizService defaultQuizService;

    @PostMapping()
    public ResponseEntity<?> addDefaultQuiz(@RequestBody QuizDTO quizDTO, HttpServletRequest request) {
        // 헤더에서 토큰 추출
        String tokenHeader = request.getHeader("Authorization");
        // "Bearer " 접두사 제거
        String token = tokenHeader != null ? tokenHeader.replace("Bearer ", "") : null;

        Boolean defaultQuiz = defaultQuizService.addQuizAndDefaultQuiz(quizDTO, token);
        if (!defaultQuiz) {
            return ResponseEntity.badRequest().body("DefaultQuizController - addDefaultQuiz()");
        }

        return ResponseEntity.ok().body("Quiz has been saved successfully");
    }

    @PostMapping("/user")
    public ResponseEntity<?> addUserQuiz(@RequestBody QuizDTO quizDTO, HttpServletRequest request) {
        // 헤더에서 토큰 추출
        String tokenHeader = request.getHeader("Authorization");
        // "Bearer " 접두사 제거
        String token = tokenHeader != null ? tokenHeader.replace("Bearer ", "") : null;

        Boolean userQuiz = defaultQuizService.addQuizAndUserQuiz(quizDTO, token);
        if(!userQuiz) {
            return ResponseEntity.badRequest().body("DefaultQuizController - addUserQuiz()");
        }

        return ResponseEntity.ok().body("Quiz has been saved successfully");
    }
}
