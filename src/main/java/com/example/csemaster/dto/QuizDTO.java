package com.example.csemaster.dto;

import com.example.csemaster.entity.ActiveUserEntity;
import com.example.csemaster.entity.QuizEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class QuizDTO {
    @Schema(hidden = true)
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

    @Schema(hidden = true)
    private Boolean isDeleted;
}
