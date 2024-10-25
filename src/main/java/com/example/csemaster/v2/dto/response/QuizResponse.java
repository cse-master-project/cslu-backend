package com.example.csemaster.v2.dto.response;

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
    private String chapter;
    private Integer quizType;
    // private double correctRate;
    private String jsonContent;
    private Boolean hasImage;
    private String creator;
}
