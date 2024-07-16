package com.example.csemaster.v1.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuizResponse {
    private Long quizId;

    private String subject;
    private String detailSubject;
    private Integer quizType;
    // private double correctRate;
    private String jsonContent;
    private LocalDateTime createAt;
    private Boolean hasImage;
}
