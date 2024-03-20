package com.example.csemaster.login.manager;

import com.example.csemaster.jwt.JwtInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerLoginController {
    private final ManagerLoginService managerLoginService;
    @Autowired
    public ManagerLoginController(ManagerLoginService managerLoginService) {
        this.managerLoginService = managerLoginService;
    }

    // db 연결 테스트용
    /*@PostMapping("/login")
    public String login(@RequestBody ManagerLoginDto managerLoginDto) { return managerLoginService.login(managerLoginDto); }*/

    @PostMapping("/login")
    public String login(@RequestBody ManagerLoginDto managerLoginDto, HttpServletResponse response) {
        // return managerLoginService.login(managerLoginDto);

        JwtInfo jwtInfo = managerLoginService.login(managerLoginDto);

        response.setHeader("Authorization", "Bearer " + jwtInfo.getAccessToken());
        response.setHeader("Refresh-Token", jwtInfo.getRefreshToken());

        return "로그인";
    }

    @PostMapping("/refresh")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        JwtInfo jwtInfo = managerLoginService.refreshToken(request.getHeader("Refresh-Token"));

        response.setHeader("Authorization", "Bearer " + jwtInfo.getAccessToken());
        response.setHeader("Refresh-Token", jwtInfo.getRefreshToken());

        return "액세스 토큰 & 리프레시 토큰 재발급";
    }

    // 액세스 토큰 테스트
    @PostMapping("/test")
    public String test() {
        return "success";
    }
}
