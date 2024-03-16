package com.example.csemaster.login.user.mapper;

import com.example.csemaster.login.user.dto.UserAccessTokenDTO;
import com.example.csemaster.login.user.entity.UserAccessTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAccessTokenMapper {
    UserAccessTokenMapper INSTANCE = Mappers.getMapper(UserAccessTokenMapper.class);

    UserAccessTokenDTO entityToDTO(UserAccessTokenEntity accessTokenEntity);
}
