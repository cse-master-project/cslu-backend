package com.example.csemaster.v2.dto.request;

import com.example.csemaster.v2.dto.ChapterDTO;
import lombok.Data;

import java.util.List;

@Data
public class SubjectSortRequest {
    private String subject;
    private List<ChapterDTO> chapters;
}
