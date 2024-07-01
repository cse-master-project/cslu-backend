package com.example.csemaster;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class CseMasterApplication {
    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        log.info("Server time zone has been configured to \"" + new Date() + " (UTC+9)\"");
    }
    public static void main(String[] args) {
        SpringApplication.run(CseMasterApplication.class, args);
        log.info("CSLU server started successfully!");
    }
}
