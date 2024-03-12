package com.example.csemaster.login.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLoginService {
    private final UserLoginRepository userLoginRepository;

    @Autowired
    public UserLoginService(UserLoginRepository userLoginRepository) {
        this.userLoginRepository = userLoginRepository;
    }

    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userLoginRepository.findAll();
        return users.stream()
                .map(UserDTO::toUserDTO)
                .collect(Collectors.toList());
    }
}
