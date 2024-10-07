package com.example.csemaster.v2.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.beans.ConstructorProperties;

@Data
public class SubjectRequest {
    @NotBlank
    private String subject;

    @ConstructorProperties({"subject"})
    public SubjectRequest(String subject) {
        this.subject = subject;
    }
}
