package com.example.csemaster.login.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ManagerLoginService {
    private final ManagerRepository managerRepository;
    private final ManagerLoginMapper managerLoginMapper;

    @Autowired
    public ManagerLoginService(ManagerRepository managerRepository, ManagerLoginMapper managerLoginMapper) {
        this.managerRepository = managerRepository;
        this.managerLoginMapper = managerLoginMapper;
    }

    // db 연결 테스트용
    public String login(ManagerLoginDto managerLoginDto) {
        Optional<ManagerModel> existingManager = managerRepository.findById(managerLoginDto.getManagerId());

        if (existingManager.isPresent()) {
            ManagerModel manager = existingManager.get();
            String managerPw = manager.getManagerPw();

            if (managerPw.equals(managerLoginDto.getManagerPw())) {
                log.info("로그인 성공");
                return "로그인";
            } else {
                log.error("비밀번호 불일치");
                return "로그인 실패";
            }

        } else {
            log.error("존재하지 않는 사용자");
            return "로그인 실패";
        }
    }

}
