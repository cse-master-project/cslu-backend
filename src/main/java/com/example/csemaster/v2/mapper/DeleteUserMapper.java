package com.example.csemaster.v2.mapper;

import com.example.csemaster.core.dao.actor.ActiveUserEntity;
import com.example.csemaster.core.dao.actor.DeleteUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface DeleteUserMapper {
    DeleteUserMapper INSTANCE = Mappers.getMapper(DeleteUserMapper.class);

    @Mapping(target = "createAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "nickname", ignore = true)
    @Mapping(target = "user", ignore = true)
    ActiveUserEntity deleteToActive(DeleteUserEntity deleteUser);
}
