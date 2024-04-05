package com.example.csemaster.features.account.user;

import com.example.csemaster.dto.response.QuizStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserProfileController {
    private final UserProfileService userProfileService;

    // 사용자 정보 조회
    @GetMapping("/info")
    private ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return userProfileService.getUserInfo(userId);
    }

    // 사용자 닉네임 설정
    @PutMapping("/info/nickname")
    private ResponseEntity<?> setUserNickname(@RequestBody String nickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return userProfileService.setUserNickname(userId, nickname);
    }

    // 사용자 문제 풀이 통계
    @GetMapping("/quiz-results")
    public ResponseEntity<QuizStatsResponse> getStatistics() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return ResponseEntity.ok().body(userProfileService.getUserQuizResult(userId));
    }
}
