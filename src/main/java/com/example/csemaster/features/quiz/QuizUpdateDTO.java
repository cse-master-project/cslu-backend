package com.example.csemaster.features.quiz;

import lombok.Data;

@Data
public class QuizUpdateDTO {
    private Long quizId;
    private String newJsonContent;
}
