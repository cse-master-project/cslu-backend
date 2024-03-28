package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.response.QuizResponse;
import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.entity.QuizLogEntity;
import com.example.csemaster.entity.QuizReportEntity;
import com.example.csemaster.mapper.QuizMapper;
import com.example.csemaster.repository.ActiveQuizRepository;
import com.example.csemaster.repository.QuizLogRepository;
import com.example.csemaster.repository.QuizReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizSolverService {
    private final ActiveQuizRepository activeQuizRepository;
    private final QuizLogRepository quizLogRepository;
    private final QuizReportRepository quizReportRepository;

    public QuizResponse getQuiz(String userId, String subject, String detailSubject) {
        List<QuizEntity> quiz = activeQuizRepository.getAnOpenQuiz(userId, subject, detailSubject);
        int randomIndex = (int)(Math.random() * quiz.size());
        return QuizMapper.INSTANCE.entityToResponse(quiz.get(randomIndex));
    }

    public void saveQuizResult(String userId, Long quizId, Boolean isCorrect) {
        Integer maxTryCnt = quizLogRepository.getMaxTryCnt(userId, quizId);

        QuizLogEntity quizLog = new QuizLogEntity();
        quizLog.setQuizId(quizId);
        quizLog.setUserId(userId);
        quizLog.setTryCnt(maxTryCnt == null ? 0 : maxTryCnt + 1);

        quizLog.setAnswerStatus(isCorrect);
        quizLog.setSolvedAt(LocalDateTime.now());

        quizLogRepository.save(quizLog);
    }

    public void saveQuizReport(String userId, Long quizId, String content) {
        QuizReportEntity quizReport = new QuizReportEntity();

        quizReport.setQuizId(quizId);
        quizReport.setUserId(userId);
        quizReport.setContent(content);
        quizReport.setReportAt(LocalDateTime.now());

        quizReportRepository.save(quizReport);
    }
}
