package com.example.csemaster.features.quiz;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class QuizLogDTO {
    private Long quizId;
    private String userId;
    private int tryCnt;

    private Boolean answerStatus;
    private LocalDateTime solvedAt;
}
