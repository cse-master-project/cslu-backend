package com.example.csemaster.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetailSubjectDTO {
    @NotBlank
    private String subject;
    @NotBlank
    private String detailSubject;

    private Integer sortIndex;
}
