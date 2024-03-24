package com.example.csemaster.features.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class QuizDTO {
    private Long quizId;

    private String subject;
    private String detailSubject;
    //private double correctRate;
    private String jsonContent;
    //private LocalDateTime createAt;
}
