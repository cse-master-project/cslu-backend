package com.example.csemaster.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    @Min(value = 1, message = "NOT_VALID_STATE")
    @Max(value = 5, message = "NOT_VALID_STATE")
    private Integer quizType;
    private String jsonContent;
    private Boolean hasImage;
    private Boolean isDeleted;
}
