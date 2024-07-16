package com.example.csemaster.v1.mapper;

import com.example.csemaster.core.dao.quiz.accessory.QuizReportEntity;
import com.example.csemaster.v1.dto.response.QuizReportResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QuizReportMapper {
    QuizReportMapper INSTANCE = Mappers.getMapper(QuizReportMapper.class);

    @Mapping(target = "userNickname", ignore = true)
    QuizReportResponse toQuizReportResponse(QuizReportEntity quizReportEntity);
    List<QuizReportResponse> toQuizReportResponseList(List<QuizReportEntity> quizReportEntities);
}
