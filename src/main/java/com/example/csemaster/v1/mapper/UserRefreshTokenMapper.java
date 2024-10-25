package com.example.csemaster.v1.mapper;

import com.example.csemaster.v1.dto.UserRefreshTokenDTO;
import com.example.csemaster.core.dao.token.UserRefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserRefreshTokenMapper {
    UserRefreshTokenMapper INSTANCE = Mappers.getMapper(UserRefreshTokenMapper.class);

    UserRefreshTokenDTO entityToDTO(UserRefreshTokenEntity refreshTokenEntity);
}
