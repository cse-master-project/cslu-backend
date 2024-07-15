package com.example.csemaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterUpdateDTO {
    @NotBlank
    private String subject;
    @NotBlank
    private String detailSubject;
    @NotBlank
    private String newDetailSubject;
}
