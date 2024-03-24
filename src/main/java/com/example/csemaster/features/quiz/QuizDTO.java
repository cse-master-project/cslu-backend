package com.example.csemaster.features.quiz;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuizDTO {
    private Long quizId;

    private String subject;
    private String detailSubject;
    private double correctRate;
    private String jsonContent;
    private LocalDateTime createAt;
}
