package com.example.csemaster.features.quiz.minyoung;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizReportResponse {
    private Long quizReportId;
    private Long quizId;
    private String userId;
    private String content;
    private LocalDateTime reportAt;
}
