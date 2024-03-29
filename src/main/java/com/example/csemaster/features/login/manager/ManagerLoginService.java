package com.example.csemaster.features.login.manager;

import com.example.csemaster.dto.ManagerLoginDTO;
import com.example.csemaster.dto.ManagerLogoutDTO;
import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import com.example.csemaster.jwt.*;
import com.example.csemaster.mapper.ManagerLogoutMapper;
import com.example.csemaster.mapper.RefreshTokenMapper;
import com.example.csemaster.repository.ManagerAccessTokenBlacklistRepository;
import com.example.csemaster.repository.ManagerRepository;
import com.example.csemaster.repository.ManagerRefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerLoginService {
    private final RefreshTokenMapper refreshTokenMapper;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final ManagerRefreshTokenRepository managerRefreshTokenRepository;
    private final ManagerLogoutMapper managerLogoutMapper;
    private final ManagerAccessTokenBlacklistRepository managerAccessTokenBlacklistRepository;

    @Transactional
    public JwtInfo login(ManagerLoginDTO managerLoginDto) {
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
        managerRefreshTokenRepository.save(managerRefreshTokenEntity);

        log.info("로그인 성공");
        return jwtInfo;
    }

    public ResponseEntity<?> logout(ManagerLogoutDTO managerLogoutDTO) {
        // 현재 유효한 액세스 토큰 블랙리스트에 추가
        ManagerAccessTokenBlacklistEntity accessTokenBlacklistEntity = managerLogoutMapper.toBlacklistEntity(managerLogoutDTO);
        managerAccessTokenBlacklistRepository.save(accessTokenBlacklistEntity);

        // 리프레시 토큰 삭제
        Optional<ManagerRefreshTokenEntity> refreshToken = managerRefreshTokenRepository.findById(managerLogoutDTO.getManagerId());
        refreshToken.ifPresent(managerRefreshTokenRepository::delete);

        return ResponseEntity.ok().build();
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // 매 60초마다 실행
    public void cleanExpiredTokens() {
        // 현재 시간 기준으로 만료된 토큰 삭제 로직 구현
        Date now = new Date();
        managerAccessTokenBlacklistRepository.deleteByExpirationTimeBefore(now);
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
        ManagerRefreshTokenEntity managerRefreshTokenEntity = managerRefreshTokenRepository.findById(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Refresh token entity not found"));

        managerRefreshTokenEntity.setRefreshToken(newJwtInfo.getRefreshToken());
        managerRefreshTokenEntity.setIssuedAt(LocalDateTime.now());
        managerRefreshTokenEntity.setExpirationTime(LocalDateTime.now().plusHours(1));
        managerRefreshTokenRepository.save(managerRefreshTokenEntity);


        log.info("액세스 토큰 및 리프레시 토큰 재발급 성공");

        return newJwtInfo;
    }
}
