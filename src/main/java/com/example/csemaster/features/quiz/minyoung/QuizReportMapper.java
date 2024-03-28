package com.example.csemaster.features.quiz.minyoung;

import com.example.csemaster.entity.QuizReportEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizReportMapper {
    QuizReportResponse toQuizReportResponse(QuizReportEntity quizReportEntity);
    List<QuizReportResponse> toQuizReportResponseList(List<QuizReportEntity> quizReportEntities);
}
