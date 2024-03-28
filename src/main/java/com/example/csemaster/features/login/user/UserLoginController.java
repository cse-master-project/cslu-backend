package com.example.csemaster.features.login.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserLoginController {
    private final UserLoginService userLoginService;

    @Autowired
    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PostMapping("/auth/google/sign-up")
    public ResponseEntity<?> signUp(@RequestBody String accessToken, String nickname) {
        String googleId = userLoginService.isGoogleAuth(accessToken);

        if (googleId != null) {
            userLoginService.createUser(googleId, nickname);

            return ResponseEntity.ok(userLoginService.getTokens(googleId));
        } else {
            // 구글 액세스토큰이 유효하지 않을 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Google login failed");
        }
    }
    @PostMapping("/auth/google")
    public ResponseEntity<?> googleLogin(@RequestBody String accessToken) {
        // 넘겨받은 액세스 토큰으로 구글 api에 검증 요청
        String googleId = userLoginService.isGoogleAuth(accessToken);

        if (googleId != null) {
            // 구글id로 DB 검색 후, 등록된 유저인 경우 토큰 발급
            String userId = userLoginService.getUserId(googleId);

            if (userId != null) {
                return ResponseEntity.ok(userLoginService.getTokens(userId));
            } else {
                // 최초 등록을 하지 않은 유저
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized: Non Sign-up user");
            }
        } else {
            // 구글 액세스토큰이 유효하지 않을 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Google login failed");
        }
    }

    @PostMapping("/auth/google/logout")
    public ResponseEntity<?> googleUserLogout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String accessToken = (String) authentication.getCredentials();

        return userLoginService.logout(userId, accessToken);
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String accessToken = (String) authentication.getCredentials();

        // 로그아웃으로 토큰 만료 후 비활성화
        userLoginService.logout(userId, accessToken);
        return userLoginService.deactivateUser(userId);
    }

    @GetMapping("/info")
    private ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return userLoginService.getUserInfo(userId);
    }

    @PostMapping("/info/nickname")
    private ResponseEntity<?> setUserNickname(@RequestBody String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return userLoginService.setUserNickname(userId, nickname);
    }
}
