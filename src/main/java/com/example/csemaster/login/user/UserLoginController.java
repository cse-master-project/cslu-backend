package com.example.csemaster.login.user;

import com.example.csemaster.login.user.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserLoginController {
    private final UserLoginService userLoginService;

    @Autowired
    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @GetMapping("/test")
    public List<UserDTO> getAllUser() throws Exception {
        System.out.println("test");
        return userLoginService.getAllUsers();
    }

    @GetMapping("/test2")
    public String test(HttpServletRequest request) {
        System.out.println("test");
        return "축하합니다";
    }

    @PostMapping("/auth/google/sign-up")
    public ResponseEntity<?> signUp(@RequestParam String accessToken, String nickname) {
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
    public ResponseEntity<?> googleLogin(@RequestParam String accessToken) {
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
}
