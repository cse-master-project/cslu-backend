package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.response.QuizResponse;
import com.example.csemaster.entity.ActiveQuizEntity;
import com.example.csemaster.entity.QuizLogEntity;
import com.example.csemaster.entity.QuizReportEntity;
import com.example.csemaster.mapper.QuizMapper;
import com.example.csemaster.repository.ActiveQuizRepository;
import com.example.csemaster.repository.QuizLogRepository;
import com.example.csemaster.repository.QuizReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
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
        List<ActiveQuizEntity> quiz = activeQuizRepository.getAnOpenQuiz(userId, subject, detailSubject);
        int randomIndex = (int)(Math.random() * quiz.size());
        return QuizMapper.INSTANCE.entityToResponse(quiz.get(randomIndex));
    }

    public ResponseEntity<?> saveQuizResult(String userId, Long quizId, Boolean isCorrect) {
        try {
            Integer maxTryCnt = quizLogRepository.getMaxTryCnt(userId, quizId);

            QuizLogEntity quizLog = new QuizLogEntity();
            quizLog.setQuizId(quizId);
            quizLog.setUserId(userId);

            quizLog.setTryCnt(maxTryCnt == null ? 1 : maxTryCnt + 1);
            quizLog.setAnswerStatus(isCorrect);
            quizLog.setSolvedAt(LocalDateTime.now());

            quizLogRepository.save(quizLog);

            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            // 잘못된 quiz id를 전달했을 때 발생하는 제약 조건 위반 예외
            return ResponseEntity.badRequest().body("Invalid value");
        }
    }

    public ResponseEntity<?> saveQuizReport(String userId, Long quizId, String content) {
        try {
            QuizReportEntity quizReport = new QuizReportEntity();

            quizReport.setQuizId(quizId);
            quizReport.setUserId(userId);
            quizReport.setContent(content);
            quizReport.setReportAt(LocalDateTime.now());

            quizReportRepository.save(quizReport);

            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            // 잘못된 quiz id를 전달했을 때 발생하는 제약 조건 위반 예외
            return ResponseEntity.badRequest().body("Invalid value");
        }

    }
}
