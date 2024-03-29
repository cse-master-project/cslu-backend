package com.example.csemaster.mapper;

import com.example.csemaster.dto.ManagerLogoutDTO;
import com.example.csemaster.features.login.manager.ManagerAccessTokenBlacklistEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManagerLogoutMapper {
    ManagerAccessTokenBlacklistEntity toBlacklistEntity(ManagerLogoutDTO managerLogoutDTO);
}
