package com.example.csemaster.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private String jsonContent;
}
