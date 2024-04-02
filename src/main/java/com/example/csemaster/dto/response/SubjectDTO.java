package com.example.csemaster.dto.response;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectDTO {
    @NotBlank
    private String subject;
}
