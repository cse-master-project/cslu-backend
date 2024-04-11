package com.example.csemaster.features.account.manager;

import com.example.csemaster.dto.ManagerLoginDTO;
import com.example.csemaster.jwt.JwtInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.csemaster.features.account.TokenUtils.extractAccessTokenFromHeader;

@Tag(name = "ManagerAccount", description = "관리자 계정 관련 기능")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerAccountController {
    private final ManagerAccountService managerAccountService;

    // 관리자 로그인
    @Operation(
            summary = "관리자 로그인",
            description = "관리자 아이디, 비밀번호를 받아서 토큰 발급"
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody ManagerLoginDTO managerLoginDto) {
        JwtInfo jwtInfo = managerAccountService.login(managerLoginDto);

        if (jwtInfo == null) {
            return ResponseEntity.badRequest().body("아이디나 비밀번호가 잘못됨");
        }

        return ResponseEntity.ok(jwtInfo);
    }

    // 관리자 로그아웃
    @Operation(
            summary = "관리자 로그아웃",
            description = "관리자 아이디, 액세스 토큰을 블랙리스트에 추가"
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();
        String accessToken = extractAccessTokenFromHeader(request);

        return managerAccountService.logout(managerId, accessToken);
    }

    // 관리자 토큰 재발급
    @Operation(
            summary = "관리자 토큰 재발급",
            description = "관리자의 리프레시 토큰을 추출하여 액세스 토큰 및 리프레시 토큰 재발급"
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        JwtInfo jwtInfo = managerAccountService.refreshToken(request.getHeader("Refresh-Token"));

        if (jwtInfo == null) {
            return ResponseEntity.badRequest().body("유효하지 않은 토큰");
        }

        return ResponseEntity.ok(jwtInfo);
    }
}
