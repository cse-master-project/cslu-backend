package com.example.csemaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectUpdateDTO {
    @NotBlank
    private String subject;
    @NotBlank
    private String newSubject;
}
