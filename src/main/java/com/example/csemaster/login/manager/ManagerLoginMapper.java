package com.example.csemaster.login.manager;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ManagerLoginMapper {
    ManagerModel toManagerModel(ManagerLoginDto managerLoginDto);
}
