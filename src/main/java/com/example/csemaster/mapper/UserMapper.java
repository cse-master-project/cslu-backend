package com.example.csemaster.mapper;

import com.example.csemaster.dto.UserDTO;
import com.example.csemaster.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO entityToDTO(UserEntity user);
}
