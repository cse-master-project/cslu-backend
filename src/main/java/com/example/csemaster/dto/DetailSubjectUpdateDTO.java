package com.example.csemaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetailSubjectUpdateDTO {
    @NotNull
    private Long subjectId;
    @NotBlank
    private String detailSubject;
    @NotBlank
    private String newDetailSubject;
}
