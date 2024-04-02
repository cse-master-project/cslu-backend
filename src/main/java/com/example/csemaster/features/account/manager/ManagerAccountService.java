package com.example.csemaster.features.account.manager;

import com.example.csemaster.dto.ManagerLoginDTO;
import com.example.csemaster.entity.AccessTokenBlackListEntity;
import com.example.csemaster.entity.ManagerRefreshTokenEntity;
import com.example.csemaster.features.account.TokenUtils;
import com.example.csemaster.jwt.JwtInfo;
import com.example.csemaster.jwt.JwtProvider;
import com.example.csemaster.mapper.RefreshTokenMapper;
import com.example.csemaster.repository.AccessTokenBlackListRepository;
import com.example.csemaster.repository.ManagerRefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerAccountService {
    private final RefreshTokenMapper refreshTokenMapper;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final ManagerRefreshTokenRepository managerRefreshTokenRepository;
    private final AccessTokenBlackListRepository accessTokenBlackListRepository;

    private void saveRefreshToken(ManagerLoginDTO managerLoginDto, JwtInfo jwtInfo) {
        // 토큰 해쉬 후 저장
        ManagerRefreshTokenEntity refreshToken = refreshTokenMapper.toRefreshTokenEntity(managerLoginDto, jwtInfo);
        refreshToken.setRefreshToken(TokenUtils.hashString(refreshToken.getRefreshToken()));
        managerRefreshTokenRepository.save(refreshToken);
    }

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

        // 4. 토큰 정보를 RefreshTokenEntity 에 저장
        saveRefreshToken(managerLoginDto, jwtInfo);

        log.info("로그인 성공 [ID:" + managerLoginDto.getManagerId() + "]");
        return jwtInfo;
    }

    @Transactional
    public ResponseEntity<?> logout(String managerId, String accessToken) {
        // 현재 유효한 액세스 토큰 블랙리스트에 추가
        AccessTokenBlackListEntity accessTokenBlackList = new AccessTokenBlackListEntity();
        accessTokenBlackList.setAccessToken(TokenUtils.hashString(accessToken));
        accessTokenBlackList.setBlackAt(LocalDateTime.now());

        accessTokenBlackListRepository.save(accessTokenBlackList);

        // 리프레시 토큰 삭제
        return managerRefreshTokenRepository.findById(managerId)
            .map(token -> {
                managerRefreshTokenRepository.delete(token);
                return ResponseEntity.ok().build();
            }).orElse(ResponseEntity.notFound().build());
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
