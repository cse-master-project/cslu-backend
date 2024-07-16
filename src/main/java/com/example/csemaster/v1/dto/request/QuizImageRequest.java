package com.example.csemaster.v1.dto.request;

import lombok.Getter;

@Getter
public class QuizImageRequest {
    private String base64String;
    private Long quizId;
}
