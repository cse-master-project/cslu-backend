package com.example.csemaster.features.quiz;

import com.example.csemaster.entity.ActiveQuizEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizSearchController {
    private final QuizSearchService quizSearchService;

    @GetMapping("/")
    public Page<ActiveQuizEntity> getAllQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getQuiz(pageRequest);
    }

    @GetMapping("/user")
    public Page<ActiveQuizEntity> getUserQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getUserQuiz(pageRequest);
    }

    @GetMapping("/default")
    public Page<ActiveQuizEntity> getDefaultQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getDefaultQuiz(pageRequest);
    }
}
