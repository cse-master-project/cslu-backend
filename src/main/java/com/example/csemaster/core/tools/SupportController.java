package com.example.csemaster.core.tools;

import com.example.csemaster.v2.domain.quiz.management.ManagementService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class SupportController {
    @Value("${log.file.path}")
    private String logPath;
    private final ManagementService managementService;

    @GetMapping("/dev/log")
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

    @PatchMapping("/api/v2/management/quiz/{quizId}")
    @Hidden
    public ResponseEntity<?> updateQuizTmp(@PathVariable Long quizId, @RequestBody String newJsonContent) {
        return managementService.updateQuiz(quizId, newJsonContent);
    }

    @DeleteMapping("/api/v2/management/quiz/{quizId}")
    @Hidden
    public ResponseEntity<?> deleteQuizTmp(@PathVariable Long quizId) {
        return managementService.deleteQuiz(quizId);
    }
}
