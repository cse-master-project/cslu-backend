package com.example.csemaster.mapper;

import com.example.csemaster.entity.QuizReportEntity;
import com.example.csemaster.dto.response.QuizReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizReportMapper {
    QuizReportMapper INSTANCE = Mappers.getMapper(QuizReportMapper.class);

    QuizReportResponse toQuizReportResponse(QuizReportEntity quizReportEntity);
    List<QuizReportResponse> toQuizReportResponseList(List<QuizReportEntity> quizReportEntities);
}
