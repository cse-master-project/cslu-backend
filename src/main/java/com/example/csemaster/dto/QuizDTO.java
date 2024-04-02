package com.example.csemaster.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String subject;
    @NotBlank
    private String detailSubject;
    private String jsonContent;
    private Boolean hasImage;
    private Boolean isDeleted;
}
