package com.example.csemaster.features.default_quiz;

import com.example.csemaster.features.quiz.QuizDTO;
import com.example.csemaster.features.quiz.QuizEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddQuizMapper {
    @Mapping(target = "quizId", ignore = true)
    @Mapping(target = "subject", source = "quizDTO.subject")
    @Mapping(target = "detailSubject", source = "quizDTO.detailSubject")
    @Mapping(target = "correctRate", ignore = true)
    @Mapping(target = "jsonContent", source = "quizDTO.jsonContent")
    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
    QuizEntity toQuizEntity(QuizDTO quizDTO);

    @AfterMapping
    default void setCorrectRate(@MappingTarget QuizEntity quizEntity) {
        quizEntity.setCorrectRate(0);
    }

    @AfterMapping
    default void setQuizId(@MappingTarget QuizEntity quizEntity) {
        quizEntity.setQuizId(null);
    }
}
