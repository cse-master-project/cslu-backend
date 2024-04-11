package com.example.csemaster;

import com.example.csemaster.repository.AccessTokenBlackListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final AccessTokenBlackListRepository accessTokenBlackListRepository;

    // 블랙리스트에 있는 만료된 토큰 1시간 마다 정리
    @Scheduled(cron = "0 0 0/1 * * *")
    public void removeBlackToken() {
        accessTokenBlackListRepository.deleteAll(accessTokenBlackListRepository.findExpiredToken());
        log.info("Remove expired token");
    }

}
