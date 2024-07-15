package com.example.csemaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectRequest {
    @NotBlank
    private String subject;
}
