package com.example.csemaster.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SubjectResponse {
    private String subject;
    private List<String> chapters;
}
