package com.example.csemaster.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChapterRequest {
    @NotBlank
    String subject;
    @NotBlank
    String chapter;
}
