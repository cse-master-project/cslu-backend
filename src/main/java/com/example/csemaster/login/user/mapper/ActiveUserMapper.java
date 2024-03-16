package com.example.csemaster.login.user.mapper;

import com.example.csemaster.login.user.dto.ActiveUserDTO;
import com.example.csemaster.login.user.entity.ActiveUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActiveUserMapper {
    ActiveUserMapper INSTANCE = Mappers.getMapper(ActiveUserMapper.class);

    ActiveUserDTO entityToDTO(ActiveUserEntity activeUser);
}
