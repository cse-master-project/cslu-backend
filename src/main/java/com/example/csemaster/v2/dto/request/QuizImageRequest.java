package com.example.csemaster.v2.dto.request;

import lombok.Getter;

@Getter
public class QuizImageRequest {
    private String base64String;
    private Long quizId;
}
