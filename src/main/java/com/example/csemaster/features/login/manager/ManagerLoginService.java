package com.example.csemaster.features.login.manager;

import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import com.example.csemaster.jwt.*;
import com.example.csemaster.mapper.RefreshTokenMapper;
import com.example.csemaster.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ManagerLoginService {
    private final ManagerRepository managerRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public ManagerLoginService(ManagerRepository managerRepository, AuthenticationManagerBuilder authenticationManagerBuilder, JwtProvider jwtProvider, RefreshTokenRepository refreshTokenRepository, RefreshTokenMapper refreshTokenMapper) {
        this.managerRepository = managerRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    @Transactional
    public JwtInfo login(ManagerLoginDto managerLoginDto) {
        // 1. managerLoginDto를 기반으로 Authentication 객체 생성
        // 이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(managerLoginDto.getManagerId(), managerLoginDto.getManagerPw());

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 ManagerModel에 대한 검증 진행
        // authenticate 메서드가 실행될 때 UserDetailsService에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtInfo jwtInfo = jwtProvider.generateToken(authentication);

        // 4. 토큰 정보를 RefreshTokenEntity에 저장
        ManagerRefreshTokenEntity managerRefreshTokenEntity = refreshTokenMapper.toRefreshTokenEntity(managerLoginDto, jwtInfo);
        refreshTokenRepository.save(managerRefreshTokenEntity);

        log.info("로그인 성공");
        return jwtInfo;
    }

    @Transactional
    public JwtInfo refreshToken(String refreshToken) {
        // 1. 리프레시 토큰 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 2. 리프레시 토큰으로부터 사용자 정보 추출
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);

        // 3. 새로운 액세스 토큰과 리프레시 토큰 생성
        JwtInfo newJwtInfo = jwtProvider.generateToken(authentication);

        // 4. 새로운 리프레시 토큰으로 RefreshTokenEntity 업데이트
        ManagerRefreshTokenEntity managerRefreshTokenEntity = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Refresh token entity not found"));

        managerRefreshTokenEntity.setRefreshToken(newJwtInfo.getRefreshToken());
        managerRefreshTokenEntity.setIssuedAt(LocalDateTime.now());
        managerRefreshTokenEntity.setExpirationTime(LocalDateTime.now().plusHours(1));
        refreshTokenRepository.save(managerRefreshTokenEntity);


        log.info("액세스 토큰 및 리프레시 토큰 재발급 성공");

        return newJwtInfo;
    }

    // db 연결 테스트용
    /*public String login(ManagerLoginDto managerLoginDto) {
        Optional<ManagerModel> existingManager = managerRepository.findById(managerLoginDto.getManagerId());

        if (existingManager.isPresent()) {
            ManagerModel manager = existingManager.get();
            String managerPw = manager.getManagerPw();

            if (managerPw.equals(managerLoginDto.getManagerPw())) {
                log.info("로그인 성공");
                return "로그인";
            } else {
                log.error("비밀번호 불일치");
                return "로그인 실패";
            }

        } else {
            log.error("존재하지 않는 사용자");
            return "로그인 실패";
        }
    }*/
}
