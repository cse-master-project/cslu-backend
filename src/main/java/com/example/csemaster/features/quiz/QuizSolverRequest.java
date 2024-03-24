package com.example.csemaster.features.quiz;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSolverRequest {
    private Long quizId;
    private Boolean isCorrect;
}
