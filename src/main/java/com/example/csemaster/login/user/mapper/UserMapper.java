package com.example.csemaster.login.user.mapper;

import com.example.csemaster.login.user.dto.UserDTO;
import com.example.csemaster.login.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO entityToDTO(UserEntity user);
}
