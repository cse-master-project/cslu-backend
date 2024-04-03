package com.example.csemaster.features.account.user;

import com.example.csemaster.features.account.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserAccountController {
    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/auth/google/sign-up")
    public ResponseEntity<?> signUp(@RequestBody String accessToken, String nickname) {
        String googleId = userAccountService.isGoogleAuth(accessToken);

        if (googleId != null) {
            userAccountService.createUser(googleId, nickname);

            return ResponseEntity.ok(userAccountService.getTokens(googleId));
        } else {
            // 구글 액세스토큰이 유효하지 않을 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: Google login failed");
        }
    }
    @PostMapping("/auth/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody String accessToken) {
        // 넘겨받은 액세스 토큰으로 구글 api에 검증 요청
        String googleId = userAccountService.isGoogleAuth(accessToken);

        if (googleId != null) {
            // 구글 id 로 DB 검색 후, 등록된 유저인 경우 토큰 발급
            String userId = userAccountService.getUserId(googleId);

            if (userId != null) {
                return ResponseEntity.ok(userAccountService.getTokens(userId));
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
    public ResponseEntity<?> googleUserLogout(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String accessToken = TokenUtils.extractAccessTokenFromHeader(request);

        return userAccountService.logout(userId, accessToken);
    }

    @PostMapping("/deactivate")
    @Transactional
    public ResponseEntity<?> deactivateUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        String accessToken = TokenUtils.extractAccessTokenFromHeader(request);

        // 로그아웃으로 토큰 만료 후 비활성화
        userAccountService.logout(userId, accessToken);
        return userAccountService.deactivateUser(userId);
    }


}
