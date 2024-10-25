package com.example.csemaster.v2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterDTO {
    @NotBlank
    private String chapter;

    @NotBlank
    private Integer sortIndex;
}
