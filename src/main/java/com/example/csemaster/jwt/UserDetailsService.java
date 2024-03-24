package com.example.csemaster.jwt;

import com.example.csemaster.entity.ManagerEntity;
import com.example.csemaster.repository.ManagerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final ManagerRepository managerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String managerId) throws UsernameNotFoundException {
        return managerRepository.findById(managerId)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    // 해당하는 User의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
    private UserDetails createUserDetails(ManagerEntity managerEntity) {
        return User.builder()
                .username(managerEntity.getUsername())
                .password(passwordEncoder.encode(managerEntity.getPassword()))
                .roles(String.valueOf(MemberRole.ADMIN))
                .build();
    }
}
