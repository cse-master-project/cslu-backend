package com.example.csemaster.v2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubjectDTO {
    private String subject;
    private List<String> chapters;
}
