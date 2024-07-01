package com.example.csemaster.features;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
@Slf4j
public class SupportController {
    @Value("${log.file.path}")
    private String logPath;

    @GetMapping("/log")
    public String getLog() {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            Path path = Paths.get(logPath + "/debug/dev-logs.log");
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            int startIndex = Math.max(0, lines.size() - 40);
            for (int i = startIndex; i < lines.size(); i++) {
                contentBuilder.append(lines.get(i))
                        .append(System.lineSeparator())
                        .append("<br>");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return contentBuilder.toString();
    }
}
