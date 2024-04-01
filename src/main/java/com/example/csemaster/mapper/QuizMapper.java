package com.example.csemaster.mapper;

import com.example.csemaster.dto.response.QuizResponse;
import com.example.csemaster.entity.ActiveQuizEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuizMapper {
    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    QuizResponse entityToResponse(ActiveQuizEntity quiz);
}
