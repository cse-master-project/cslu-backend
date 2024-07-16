package com.example.csemaster.v1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DetailSubjectUpdateDTO {
    @NotBlank
    private String subject;
    @NotBlank
    private String detailSubject;
    @NotBlank
    private String newDetailSubject;
}
