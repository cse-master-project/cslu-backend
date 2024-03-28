package com.example.csemaster.features.quiz.minyoung;

import com.example.csemaster.dto.response.QuizRejectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("userQuiz")
public class NewQuizController {
    private final NewQuizService newQuizService;

    @GetMapping()
    public List<UserQuizResponse> getUserQuiz(@RequestParam String userId) {
        return newQuizService.getUserQuiz(userId);
    }

    @GetMapping("/reject")
    public List<QuizRejectResponse> getQuizReject(@RequestParam Long quizId) {
        return newQuizService.getQuizReject(quizId);
    }
}