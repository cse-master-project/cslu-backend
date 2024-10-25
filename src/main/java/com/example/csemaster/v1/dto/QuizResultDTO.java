package com.example.csemaster.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuizResultDTO {
    private Long quizId;
    private int tryCnt;
    private String subject;
    private boolean isCorrect;
}
