package com.example.csemaster.mapper;

import com.example.csemaster.dto.QuizRejectDTO;
import com.example.csemaster.entity.QuizRejectEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizRejectMapper {
    QuizRejectEntity toQuizRejectEntity(QuizRejectDTO quizRejectDTO);
}
