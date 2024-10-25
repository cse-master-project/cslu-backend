package com.example.csemaster.v1.mapper;

import com.example.csemaster.v1.dto.UserDTO;
import com.example.csemaster.core.dao.actor.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO entityToDTO(UserEntity user);
}
