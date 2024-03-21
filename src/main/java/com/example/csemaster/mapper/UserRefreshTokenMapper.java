package com.example.csemaster.mapper;

import com.example.csemaster.dto.UserRefreshTokenDTO;
import com.example.csemaster.entity.UserRefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserRefreshTokenMapper {
    UserRefreshTokenMapper INSTANCE = Mappers.getMapper(UserRefreshTokenMapper.class);

    UserRefreshTokenDTO entityToDTO(UserRefreshTokenEntity refreshTokenEntity);
}
