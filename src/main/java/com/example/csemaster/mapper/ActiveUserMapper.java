package com.example.csemaster.mapper;

import com.example.csemaster.dto.ActiveUserDTO;
import com.example.csemaster.dto.response.UserInfoResponse;
import com.example.csemaster.entity.ActiveUserEntity;
import com.example.csemaster.entity.DeleteUserEntity;
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
