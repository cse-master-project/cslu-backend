package com.example.csemaster.v1.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SubjectResponse {
    private Long subjectId;
    private String subject;
    private List<String> detailSubject;
}
