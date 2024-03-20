package com.example.csemaster.login.manager;

import com.example.csemaster.jwt.*;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ManagerLoginService {
    private final ManagerRepository managerRepository;
    private final ManagerLoginMapper managerLoginMapper;

    private final RefreshTokenMapper refreshTokenMapper;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public ManagerLoginService(ManagerRepository managerRepository, ManagerLoginMapper managerLoginMapper, AuthenticationManagerBuilder authenticationManagerBuilder, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository, RefreshTokenMapper refreshTokenMapper) {
        this.managerRepository = managerRepository;
        this.managerLoginMapper = managerLoginMapper;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenMapper = refreshTokenMapper;
    }

    @Transactional
    public JwtToken login(ManagerLoginDto managerLoginDto) {
        // 1. managerLoginDto를 기반으로 Authentication 객체 생성
        // 이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(managerLoginDto.getManagerId(), managerLoginDto.getManagerPw());

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 ManagerModel에 대한 검증 진행
        // authenticate 메서드가 실행될 때 UserDetailsService에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 4. 토큰 정보를 AccessTokenEntity에 저장

        /*LocalDateTime now = LocalDateTime.now();

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setManagerId(managerLoginDto.getManagerId());
        refreshTokenEntity.setRefreshToken(jwtToken.getAccessToken());
        refreshTokenEntity.setIssuedAt(now);
        refreshTokenEntity.setExpirationTime(now.plusDays(1));

        accessTokenRepository.save(refreshTokenEntity);*/

        RefreshTokenEntity refreshTokenEntity = refreshTokenMapper.toRefreshTokenEntity(managerLoginDto, jwtToken);
        refreshTokenRepository.save(refreshTokenEntity);

        log.info("로그인 성공");
        return jwtToken;
    }

    @Transactional
    public JwtToken refreshToken(String refreshToken) {
        // 1. 리프레시 토큰 검증
        if (!jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // 2. 리프레시 토큰으로부터 사용자 정보 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        // 3. 새로운 액세스 토큰과 리프레시 토큰 생성
        JwtToken newJwtToken = jwtTokenProvider.generateToken(authentication);

        // 4. 새로운 리프레시 토큰으로 RefreshTokenEntity 업데이트
        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Refresh token entity not found"));

        refreshTokenEntity.setRefreshToken(newJwtToken.getRefreshToken());
        refreshTokenEntity.setIssuedAt(LocalDateTime.now());
        refreshTokenEntity.setExpirationTime(LocalDateTime.now().plusHours(1));
        refreshTokenRepository.save(refreshTokenEntity);


        log.info("액세스 토큰 및 리프레시 토큰 재발급 성공");

        return newJwtToken;
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
