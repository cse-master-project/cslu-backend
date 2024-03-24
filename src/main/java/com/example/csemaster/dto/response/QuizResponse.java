package com.example.csemaster.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuizResponse {
    private Long quizId;

    private String subject;
    private String detailSubject;
    private double correctRate;
    private String jsonContent;
}
