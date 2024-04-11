package com.example.csemaster.dto.response;

import com.example.csemaster.entity.DetailSubjectEntity;
import com.example.csemaster.entity.SubjectEntity;
import lombok.Data;

import java.util.List;

@Data
public class SubjectResponse {
    private Long subjectId;
    private String subject;
    private List<String> detailSubject;
}
