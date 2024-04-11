package com.example.csemaster.dto.request;

import lombok.Getter;

@Getter
public class QuizImageRequest {
    private String base64String;
    private Long quizId;
}
