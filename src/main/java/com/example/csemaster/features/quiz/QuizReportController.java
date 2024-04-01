package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.response.QuizReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class QuizReportController {
    private final QuizReportService quizReportService;

    @GetMapping("/quiz")
    public QuizReportResponse getQuizReport(@RequestParam Long quizReportId) {
        return quizReportService.getQuizReport(quizReportId);
    }

    @GetMapping()
    public List<QuizReportResponse> allQuitReport() {
        return quizReportService.allQuizReport();
    }
}
