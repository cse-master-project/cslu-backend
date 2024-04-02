package com.example.csemaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubjectUpdateDTO {
    @NotNull
    private Long subjectId;
    @NotBlank
    private String newSubject;
}
