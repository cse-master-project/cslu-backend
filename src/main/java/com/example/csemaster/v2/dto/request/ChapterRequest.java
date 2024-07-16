package com.example.csemaster.v2.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterRequest {
    @NotBlank
    String subject;
    @NotBlank
    String chapter;
}
