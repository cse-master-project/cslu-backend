package com.example.csemaster;

import com.example.csemaster.core.config.CustomBeanNameGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.Set;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@Slf4j
@ComponentScan(nameGenerator = CustomBeanNameGenerator.class)
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
