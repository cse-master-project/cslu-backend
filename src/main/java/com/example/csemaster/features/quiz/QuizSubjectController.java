package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.response.SubjectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz-subject")
public class QuizSubjectController {

    private final QuizSubjectService quizSubjectService;

    @GetMapping()
    public List<SubjectResponse> getAllSubject() {
        return quizSubjectService.getAllSubject();
    }
}
