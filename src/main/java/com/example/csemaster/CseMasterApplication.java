package com.example.csemaster;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class CseMasterApplication {

    @PostConstruct
    public void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        System.out.println("현재시간 : " + new Date());
    }
    public static void main(String[] args) {

        SpringApplication.run(CseMasterApplication.class, args);
        System.out.println("hello world!!");
    }

}
