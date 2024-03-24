package com.example.csemaster.features.quiz;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuizMapper {
    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    QuizResponse entityToResponse(QuizEntity quiz);
}
