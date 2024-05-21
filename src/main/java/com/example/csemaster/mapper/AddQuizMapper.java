package com.example.csemaster.mapper;

import com.example.csemaster.dto.QuizDTO;
import com.example.csemaster.entity.QuizEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddQuizMapper {
    @Mapping(target = "quizId", ignore = true)
    @Mapping(target = "subject", source = "quizDTO.subject")
    @Mapping(target = "detailSubject", source = "quizDTO.detailSubject")
    @Mapping(target = "quizType", source = "quizDTO.quizType")
    @Mapping(target = "correctRate", ignore = true)
    @Mapping(target = "jsonContent", source = "quizDTO.jsonContent")
    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "hasImage", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    QuizEntity toQuizEntity(QuizDTO quizDTO);

    @AfterMapping
    default void setCorrectRate(@MappingTarget QuizEntity quizEntity) {
        quizEntity.setCorrectRate(0);
    }

    @AfterMapping
    default void setQuizId(@MappingTarget QuizEntity quizEntity) {
        quizEntity.setQuizId(null);
    }

    @AfterMapping
    default void setHasImage(@MappingTarget QuizEntity quizEntity) {
        quizEntity.setHasImage(false);
    }

    @AfterMapping
    default void setIsDeleted(@MappingTarget QuizEntity quizEntity) {
        quizEntity.setIsDeleted(false);
    }
}
