package com.example.csemaster.mapper;

import com.example.csemaster.dto.ManagerLoginDTO;
import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import com.example.csemaster.jwt.JwtInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "managerId", source = "managerLoginDto.managerId")
    @Mapping(target = "refreshToken", source = "jwtInfo.refreshToken")
    @Mapping(target = "issuedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "expirationTime", expression = "java(java.time.LocalDateTime.now().plusHours(1))")
    ManagerRefreshTokenEntity toRefreshTokenEntity(ManagerLoginDTO managerLoginDto, JwtInfo jwtInfo);
}
