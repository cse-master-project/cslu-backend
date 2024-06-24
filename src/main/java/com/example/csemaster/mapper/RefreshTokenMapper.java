package com.example.csemaster.mapper;

import com.example.csemaster.dto.ManagerLoginDTO;
import com.example.csemaster.entity.ManagerEntity;
import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import com.example.csemaster.jwt.JwtInfo;
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
