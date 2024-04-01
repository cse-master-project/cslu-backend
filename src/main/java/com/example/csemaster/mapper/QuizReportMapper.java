package com.example.csemaster.mapper;

import com.example.csemaster.entity.QuizReportEntity;
import com.example.csemaster.dto.response.QuizReportResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizReportMapper {
    QuizReportResponse toQuizReportResponse(QuizReportEntity quizReportEntity);
    List<QuizReportResponse> toQuizReportResponseList(List<QuizReportEntity> quizReportEntities);
}
