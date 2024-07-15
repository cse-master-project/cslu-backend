package com.example.csemaster.mapper;

import com.example.csemaster.dto.SubjectDTO;
import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.entity.ChapterEntity;
import com.example.csemaster.entity.SubjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SubjectMapper {
    SubjectMapper INSTANCE = Mappers.getMapper(SubjectMapper.class);

    SubjectDTO toDto(List<ChapterEntity> chapter);

    @Mapping(target = "subject", source = "entity.getSubject()")
    @Mapping(target = "chapter", source = "entity.getChapters().stream().map(ChapterEntity::getChapter).toList()")
    SubjectResponse toResponse(SubjectEntity entity);
}
