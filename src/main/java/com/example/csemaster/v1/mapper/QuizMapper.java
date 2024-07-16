package com.example.csemaster.v1.mapper;

import com.example.csemaster.v1.dto.response.QuizResponse;
import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuizMapper {
    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    @Mapping(target = "detailSubject", source = "quiz.chapter")
    QuizResponse entityToResponse(ActiveQuizEntity quiz);
}
