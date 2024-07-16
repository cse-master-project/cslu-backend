package com.example.csemaster.v1.mapper;

import com.example.csemaster.v1.dto.ActiveUserDTO;
import com.example.csemaster.v1.dto.response.UserInfoResponse;
import com.example.csemaster.core.dao.actor.ActiveUserEntity;
import com.example.csemaster.core.dao.actor.DeleteUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ActiveUserMapper {
    ActiveUserMapper INSTANCE = Mappers.getMapper(ActiveUserMapper.class);

    ActiveUserDTO entityToDTO(ActiveUserEntity activeUser);

    @Mapping(target = "deleteAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    DeleteUserEntity activeToDelete(ActiveUserEntity activeUser);

    UserInfoResponse toUserInfo(ActiveUserEntity activeUser);
}
