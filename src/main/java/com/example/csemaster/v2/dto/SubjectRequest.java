package com.example.csemaster.v2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubjectRequest {
    @NotBlank
    private String subject;
}
