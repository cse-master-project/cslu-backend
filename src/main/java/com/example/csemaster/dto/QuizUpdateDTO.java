package com.example.csemaster.dto;

import lombok.Data;

@Data
public class QuizUpdateDTO {
    private Long quizId;
    private String newJsonContent;
}
