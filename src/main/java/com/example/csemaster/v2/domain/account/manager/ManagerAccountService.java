package com.example.csemaster.v2.domain.account.manager;

import com.example.csemaster.v2.dto.ManagerLoginDTO;
import com.example.csemaster.core.dao.token.AccessTokenBlackListEntity;
import com.example.csemaster.core.dao.actor.ManagerEntity;
import com.example.csemaster.core.dao.token.ManagerRefreshTokenEntity;
import com.example.csemaster.core.security.JwtProvider;
import com.example.csemaster.core.tools.TokenUtils;
import com.example.csemaster.core.security.JwtInfo;
import com.example.csemaster.v2.mapper.RefreshTokenMapper;
import com.example.csemaster.core.repository.AccessTokenBlackListRepository;
import com.example.csemaster.core.repository.ManagerRefreshTokenRepository;
import com.example.csemaster.core.repository.ManagerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service(value = "V2ManagerAccountService")
@RequiredArgsConstructor
public class ManagerAccountService {
    private final RefreshTokenMapper refreshTokenMapper;
    private final ManagerRepository managerRepository;
    private final JwtProvider jwtProvider;
    private final ManagerRefreshTokenRepository managerRefreshTokenRepository;
    private final AccessTokenBlackListRepository accessTokenBlackListRepository;

    private void saveRefreshToken(ManagerEntity manager, JwtInfo jwtInfo) {
        // 토큰 해쉬 후 저장
        ManagerRefreshTokenEntity refreshToken = refreshTokenMapper.toRefreshTokenEntity(manager, jwtInfo);
        refreshToken.setRefreshToken(TokenUtils.hashString(refreshToken.getRefreshToken()));
        managerRefreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public JwtInfo login(ManagerLoginDTO managerLoginDto) {
        // 1. managerLoginDto를 기반으로 객체 생성 및 검증
        Optional<ManagerEntity> manager = managerRepository.findById(managerLoginDto.getManagerId());
        if (manager.isEmpty()) {
            log.debug("존재하지 않는 ID");
            return null;
        }
        if (!managerLoginDto.getManagerPw().equals(manager.get().getManagerPw())) {
            log.debug("PW 불일치");
            return null;
        }

        // 2. 인증 정보를 기반으로 JWT 토큰 생성
        JwtInfo jwtInfo = jwtProvider.generateToken(managerLoginDto.getManagerId());

        // 3. 토큰 정보를 RefreshTokenEntity 에 저장
        saveRefreshToken(manager.get(), jwtInfo);

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

        log.info("로그아웃 [ID: " + managerId + "]");
        // 리프레시 토큰 삭제
        return managerRefreshTokenRepository.findById(managerId)
            .map(token -> {
                managerRefreshTokenRepository.delete(token);
                return ResponseEntity.ok().build();
            }).orElse(ResponseEntity.notFound().build());
    }

    @Transactional
    public JwtInfo refreshToken(String refreshToken) {
        System.out.println(jwtProvider.validateRefreshToken(refreshToken));
        // 1. 리프레시 토큰 검증
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            log.debug("유효하지 않은 토큰");
            return null;
        }

        // 2. 리프레시 토큰으로부터 사용자 정보 추출
        String managerId = jwtProvider.getIdFromRefreshToken(refreshToken);
        if (managerId == null || managerId.isEmpty()) {
            log.debug("토큰으로부터 관리자 정보 추출 실패");
            return null;
        }

        // 3. 새로운 액세스 토큰과 리프레시 토큰 생성
        JwtInfo newJwtInfo = jwtProvider.generateToken(managerId);

        // 4. 새로운 리프레시 토큰으로 RefreshTokenEntity 업데이트
        ManagerRefreshTokenEntity managerRefreshTokenEntity = managerRefreshTokenRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Refresh token entity not found"));

        managerRefreshTokenEntity.setRefreshToken(newJwtInfo.getRefreshToken());
        managerRefreshTokenEntity.setIssuedAt(LocalDateTime.now());
        managerRefreshTokenEntity.setExpirationTime(LocalDateTime.now().plusHours(1));
        managerRefreshTokenRepository.save(managerRefreshTokenEntity);


        log.info("액세스 토큰 및 리프레시 토큰 재발급 성공");

        return newJwtInfo;
    }
}
