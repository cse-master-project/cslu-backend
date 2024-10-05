package com.example.csemaster.v2.domain.quiz.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
public class ImageRemover {

    @Value("${img.file.path}")
    private String imgPath;
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24시간

    // temp에 있는 임시 이미지 12시간마다 정리
    @Scheduled(cron = "0 0 0/12 * * *")
    public void removeTempImage() {
        File tempDir = new File(imgPath + "/temp");
        File[] files = tempDir.listFiles();

        if (files == null) return; // 폴더가 존재하지 않을 경우 메소드 종료

        // 24시간 지난 파일 삭제
        long cutoff = System.currentTimeMillis() - EXPIRATION_TIME;
        System.out.println(cutoff);

        for (File file : files) {
            if (file.lastModified() < cutoff) {
                file.delete();
            }
        }

        log.info("Remove temporary image");
    }

}
