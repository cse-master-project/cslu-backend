package com.example.csemaster.features.account.user;

import com.example.csemaster.entity.*;
import com.example.csemaster.features.account.TokenUtils;
import com.example.csemaster.jwt.JwtInfo;
import com.example.csemaster.jwt.JwtProvider;
import com.example.csemaster.mapper.ActiveUserMapper;
import com.example.csemaster.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountService {
    private final UserRepository userRepository;
    private final ActiveUserRepository activeUserRepository;
    private final UserRefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;
    private final AccessTokenBlackListRepository accessTokenBlackListRepository;
    private final DeleteUserRepository deleteUserRepository;

    public String isGoogleAuth(String accessToken) {
        try {
            // 구글 API에 액세스토큰을 넘겨서 유저의 정보를 확인함
            RestTemplate restTemplate = new RestTemplate();
            String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";
            String requestUri = userInfoEndpoint + "?access_token=" + accessToken;
            String response = restTemplate.getForObject(requestUri, String.class);
            // System.out.println(response);

            try {
                // 리스폰 받은 json 정보 파싱 후, google id만 반환
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response);

                return jsonNode.get("sub").asText();
            } catch (JsonProcessingException e) {
                log.debug("json 정보 파싱 실패");
                return null;
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            // 401이 반환되면 예외가 발생하며, 액세스 토큰의 인증이 실패 했다는 의미
            log.debug("액세스 토큰 인증 실패");
            return null;
        }
    }

    public boolean getRegistered(String googleId) {
        ActiveUserEntity user = activeUserRepository.findByGoogleId(googleId);

        // 가입 여부 반환
        return user != null;
    }

    public String getUserIdFromDeleteUser(String googleId) {
        DeleteUserEntity user = deleteUserRepository.findByGoogleId(googleId);
        if (user != null) return user.getUserId();
        else return null;
    }

    @Transactional
    public String rejoin(String userId, String googleId, String nickname) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setIsActive(true);
            userRepository.save(user);
        }

        ActiveUserEntity rejoinUser = new ActiveUserEntity();
        rejoinUser.setUserId(userId);
        rejoinUser.setGoogleId(googleId);
        rejoinUser.setCreateAt(LocalDateTime.now());
        rejoinUser.setNickname(nickname);

        activeUserRepository.save(rejoinUser);

        deleteUserRepository.deleteById(userId);

        log.info(rejoinUser.getUserId() + " : 재가입");
        return userId;
    }

    public String getUserId(String googleId) {
        ActiveUserEntity user = activeUserRepository.findByGoogleId(googleId);
        if (user != null) {
            return user.getUserId();
        } else {
            log.debug("존재하지 않는 구글 ID");
            return null;
        }
    }
    public JwtInfo getTokens(String userId) {
        JwtInfo token = jwtProvider.generateToken(userId);

        UserRefreshTokenEntity refreshToken = new UserRefreshTokenEntity();
        refreshToken.setUserId(userId);
        refreshToken.setRefreshToken(TokenUtils.hashString(token.getRefreshToken()));
        refreshToken.setIssueAt(token.getRefreshIseAt());
        refreshToken.setExpAt(token.getRefreshExpAt());

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    @Transactional
    public String createUser(String googleId, String nickname) {
        UserEntity user = new UserEntity();
        UUID uuid = UUID.randomUUID();
        user.setUserId(uuid.toString());
        user.setIsActive(true);
        userRepository.save(user);

        ActiveUserEntity activeUser = new ActiveUserEntity();
        activeUser.setUserId(uuid.toString());
        activeUser.setGoogleId(googleId);
        activeUser.setNickname(nickname);
        activeUser.setCreateAt(LocalDateTime.now());
        activeUserRepository.save(activeUser);

        log.info("회원가입 성공 [닉네임: " + activeUser.getNickname() + "]");
        return user.getUserId();
    }

    public ResponseEntity<?> logout(String userId, String accessToken) {
        // 현재 유효한 엑세스토큰을 블랙 처리 후 리프레시 토큰은 DB에서 삭제
        AccessTokenBlackListEntity accessTokenBlackList = new AccessTokenBlackListEntity();
        accessTokenBlackList.setAccessToken(TokenUtils.hashString(accessToken));
        accessTokenBlackList.setBlackAt(LocalDateTime.now());
        accessTokenBlackListRepository.save(accessTokenBlackList);

        Optional<UserRefreshTokenEntity> refreshToken = refreshTokenRepository.findById(userId);
        refreshToken.ifPresent(refreshTokenRepository::delete);

        Optional<ActiveUserEntity> activeUser = activeUserRepository.findById(userId);
        log.info("로그아웃 [닉네임: " + activeUser.get().getNickname() + "]");
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> deactivateUser(String userId) {
        Optional<ActiveUserEntity> activeUser = activeUserRepository.findById(userId);
        log.info("회원 탈퇴 [닉네임: " + activeUser.get().getNickname() + "]");
        return activeUserRepository.findById(userId)
                        .map(this::handleUserDeactivation)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<Void> handleUserDeactivation(ActiveUserEntity activeUser) {
        // 유저 비황성화
        activeUser.getUser().setIsActive(false);
        userRepository.save(activeUser.getUser());

        // Active에서 Delete로 이동
        DeleteUserEntity deleteUser = ActiveUserMapper.INSTANCE.activeToDelete(activeUser);
        deleteUser.setDeleteAt(LocalDateTime.now());
        deleteUserRepository.save(deleteUser);

        // 이동 후 Active 테이블에서 삭제
        activeUserRepository.delete(activeUser);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> refreshToken(String refreshToken) {
        // 1. 리프레시 토큰 검증
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            // 2. 리프레시 토큰으로부터 사용자 정보 추출
            String userId = jwtProvider.getIdFromRefreshToken(refreshToken);
            // 유저인지 매니저인지 구분
            if (userId.length() == 36) {
                // 3. 새로운 액세스 토큰과 리프레시 토큰 생성
                JwtInfo newJwtInfo = jwtProvider.generateToken(userId);

                Optional<ActiveUserEntity> activeUser = activeUserRepository.findById(userId);
                log.info("토큰 재발급 [닉네임: " + activeUser.get().getNickname() + "]");
                return refreshTokenRepository.findById(userId)
                        .map(token -> {
                            token.setRefreshToken(newJwtInfo.getRefreshToken());
                            token.setIssueAt(newJwtInfo.getRefreshIseAt());
                            token.setExpAt(newJwtInfo.getRefreshExpAt());
                            return ResponseEntity.ok().body(newJwtInfo);
                        }).orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
            } else {
                log.debug("User가 아님");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid User Type");
            }
        } else {
            log.debug("유효하지 않은 리프레시 토큰");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }
}
