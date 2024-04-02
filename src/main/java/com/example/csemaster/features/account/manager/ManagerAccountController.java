package com.example.csemaster.features.account.manager;

import com.example.csemaster.dto.ManagerLoginDTO;
import com.example.csemaster.jwt.JwtInfo;
import com.example.csemaster.jwt.JwtProvider;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manager")
public class ManagerAccountController {
    private final ManagerAccountService managerAccountService;
    private final JwtProvider jwtProvider;

    @PostMapping("/login")
    public JwtInfo login(@RequestBody ManagerLoginDTO managerLoginDto) {
        JwtInfo jwtInfo = managerAccountService.login(managerLoginDto);

        log.info("로그인");
        return jwtInfo;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();
        String accessToken = extractAccessTokenFromHeader(request);

        return managerAccountService.logout(managerId, accessToken);
    }

    @PostMapping("/refresh")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        JwtInfo jwtInfo = managerAccountService.refreshToken(request.getHeader("Refresh-Token"));

        response.setHeader("Authorization", "Bearer " + jwtInfo.getAccessToken());
        response.setHeader("Refresh-Token", jwtInfo.getRefreshToken());

        return "액세스 토큰 & 리프레시 토큰 재발급";
    }
}
