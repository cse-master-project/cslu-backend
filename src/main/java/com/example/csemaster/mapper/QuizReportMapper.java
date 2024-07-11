package com.example.csemaster.mapper;

import com.example.csemaster.entity.QuizReportEntity;
import com.example.csemaster.dto.response.QuizReportResponse;
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
