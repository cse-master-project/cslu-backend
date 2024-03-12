package com.example.csemaster.login.manager;

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
    public ManagerLoginController(ManagerLoginService managerLoginService) { this.managerLoginService = managerLoginService; }

    // db 연결 테스트용
    @PostMapping("/login")
    public String login(@RequestBody ManagerLoginDto managerLoginDto) { return managerLoginService.login(managerLoginDto); }
}
