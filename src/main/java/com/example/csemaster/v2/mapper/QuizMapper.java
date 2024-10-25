package com.example.csemaster.v2.mapper;

import com.example.csemaster.core.dao.quiz.core.AllQuizEntity;
import com.example.csemaster.v2.dto.response.QuizResponse;
import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuizMapper {
    QuizMapper INSTANCE = Mappers.getMapper(QuizMapper.class);

    QuizResponse entityToResponse(ActiveQuizEntity quiz);
    QuizResponse entityToResponse(AllQuizEntity quiz);
}
