package com.example.csemaster.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizSolverRequest {
    private Long quizId;
    private Boolean isCorrect;
}
