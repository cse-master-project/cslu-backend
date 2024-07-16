package com.example.csemaster.v2.mapper;

import com.example.csemaster.core.dao.actor.ManagerEntity;
import com.example.csemaster.core.dao.token.ManagerRefreshTokenEntity;
import com.example.csemaster.core.security.JwtInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {
    @Mapping(target = "managerId", source = "manager.managerId")
    @Mapping(target = "refreshToken", source = "jwtInfo.refreshToken")
    @Mapping(target = "issuedAt", source = "jwtInfo.refreshIseAt")
    @Mapping(target = "expirationTime", source = "jwtInfo.refreshExpAt")
    ManagerRefreshTokenEntity toRefreshTokenEntity(ManagerEntity manager, JwtInfo jwtInfo);
}
