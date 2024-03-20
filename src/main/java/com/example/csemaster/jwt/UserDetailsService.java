package com.example.csemaster.jwt;

import com.example.csemaster.login.manager.ManagerLoginDto;
import com.example.csemaster.login.manager.ManagerModel;
import com.example.csemaster.login.manager.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String managerId) throws UsernameNotFoundException {
        return managerRepository.findById(managerId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    // 해당하는 User의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(ManagerModel managerModel) {
        return User.builder()
                .username(managerModel.getUsername())
                .password(passwordEncoder.encode(managerModel.getPassword()))
                .roles(managerModel.getRoles().toArray(new String[0]))
                .build();
    }

    // -----------------------------------



}
