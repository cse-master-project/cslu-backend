package com.example.csemaster.features.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizSolverService {
    private final QuizRepository quizRepository;
    private final QuizLogRepository quizLogRepository;
    private final QuizReportRepository quizReportRepository;

    @Autowired
    public QuizSolverService(QuizRepository quizRepository, QuizLogRepository quizLogRepository, QuizReportRepository quizReportRepository) {
        this.quizRepository = quizRepository;
        this.quizLogRepository = quizLogRepository;
        this.quizReportRepository = quizReportRepository;
    }

    public QuizResponse getQuiz(String userId, String subject, String detailSubject) {
        List<QuizEntity> quiz = quizRepository.getAnOpenQuiz(userId, subject, detailSubject);
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
