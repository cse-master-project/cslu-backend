package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.response.QuizReportResponse;
import com.example.csemaster.entity.QuizReportEntity;
import com.example.csemaster.mapper.QuizReportMapper;
import com.example.csemaster.repository.QuizReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizReportService {
    private final QuizReportRepository quizReportRepository;
    private final QuizReportMapper quizReportMapper;
    public QuizReportResponse getQuizReport(Long quizReportId) {
        Optional<QuizReportEntity> quizReport = quizReportRepository.findByQuizReportId(quizReportId);

        // quizReportId가 존재하는지 확인
        if (quizReport.isEmpty()) {
            throw new RuntimeException("Incorrect quizReportId");
        }

        return quizReportMapper.toQuizReportResponse(quizReport.get());
    }

    public List<QuizReportResponse> allQuizReport() {
        List<QuizReportEntity> quizReports = quizReportRepository.findAll();
        return quizReportMapper.toQuizReportResponseList(quizReports);
    }

    public List<QuizReportResponse> getAllReportForQuiz(Long quizId) {
        return quizReportRepository.findByQuizId(quizId).stream().map(QuizReportMapper.INSTANCE::toQuizReportResponse)
                .collect(Collectors.toList());
    }
}
