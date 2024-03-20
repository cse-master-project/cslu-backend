package com.example.csemaster.login.user.mapper;

import com.example.csemaster.login.user.dto.UserRefreshTokenDTO;
import com.example.csemaster.login.user.entity.UserRefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserRefreshTokenMapper {
    UserRefreshTokenMapper INSTANCE = Mappers.getMapper(UserRefreshTokenMapper.class);

    UserRefreshTokenDTO entityToDTO(UserRefreshTokenEntity refreshTokenEntity);
}
