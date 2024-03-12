package com.example.csemaster.login.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
public class UserEntity {
    @Id
    private String userId;

    @Column(nullable = false)
    private Boolean isActive;

    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userDTO.getUserId());
        userEntity.setIsActive(userDTO.getIsActive());

        return userEntity;
    }
}
