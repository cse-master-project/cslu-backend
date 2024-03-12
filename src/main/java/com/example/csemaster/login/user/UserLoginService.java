package com.example.csemaster.login.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLoginService {
    private final UserRepository userRepository;

    @Autowired
    public UserLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(UserDTO::toUserDTO)
                .collect(Collectors.toList());
    }
}
