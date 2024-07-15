package com.example.csemaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterDTO {
    @NotBlank
    private String detailSubject;

    @NotBlank
    private Integer sortIndex;
}
