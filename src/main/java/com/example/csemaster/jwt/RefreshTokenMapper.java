package com.example.csemaster.jwt;

import com.example.csemaster.login.manager.ManagerLoginDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "managerId", source = "managerLoginDto.managerId")
    @Mapping(target = "refreshToken", source = "jwtInfo.accessToken")
    @Mapping(target = "issuedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "expirationTime", expression = "java(java.time.LocalDateTime.now().plusHours(1))")
    ManagerRefreshTokenEntity toRefreshTokenEntity(ManagerLoginDto managerLoginDto, JwtInfo jwtInfo);
}
