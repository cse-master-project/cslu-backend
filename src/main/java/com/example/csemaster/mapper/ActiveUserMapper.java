package com.example.csemaster.mapper;

import com.example.csemaster.dto.ActiveUserDTO;
import com.example.csemaster.entity.ActiveUserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActiveUserMapper {
    ActiveUserMapper INSTANCE = Mappers.getMapper(ActiveUserMapper.class);

    ActiveUserDTO entityToDTO(ActiveUserEntity activeUser);
}
