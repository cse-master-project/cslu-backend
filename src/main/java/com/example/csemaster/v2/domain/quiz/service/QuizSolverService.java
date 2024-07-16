package com.example.csemaster.v2.domain.quiz.service;

import com.example.csemaster.core.repository.*;
import com.example.csemaster.v2.dto.response.QuizResponse;
import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import com.example.csemaster.core.dao.quiz.accessory.QuizLogEntity;
import com.example.csemaster.core.dao.quiz.accessory.QuizReportEntity;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.v2.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service(value = "V2QuizSolverService")
@Slf4j
@RequiredArgsConstructor
public class QuizSolverService {
    private final ActiveQuizRepository activeQuizRepository;
    private final QuizLogRepository quizLogRepository;
    private final QuizReportRepository quizReportRepository;
    private final UserQuizRepository userQuizRepository;
    private final DefaultQuizRepository defaultQuizRepository;

    @Value("${img.file.path}")
    private String imgPath;

    public QuizResponse getQuiz(String userId, String subject, List<String> chapters, boolean hasUserQuiz, boolean hasDefaultQuiz, boolean hasSolvedQuiz) {
        // Chapter 가 비었는지 확인 & Chapter 가 유효한지 검증
        List<ActiveQuizEntity> quiz;

        // Chapter 에 데이터가 없으면 모든 세부 목차 검색
        if (chapters == null || chapters.isEmpty()) {
            // 푼 퀴즈를 나오게 설정한 경우 포함해서 검색
            if (hasSolvedQuiz) quiz = activeQuizRepository.getAnOpenQuizWithSolved(subject);
            else quiz = activeQuizRepository.getAnOpenQuiz(userId, subject);
        } else {
            if (hasSolvedQuiz) quiz = activeQuizRepository.getAnOpenQuizWithSolved(subject, chapters);
            else quiz = activeQuizRepository.getAnOpenQuiz(userId, subject, chapters);
        }
        if (!quiz.isEmpty()) {
            // 필터링을 통해 사용자 문제와 기본 문제 설정에 따라 제거
            quiz = quizFiltering(quiz, hasUserQuiz, hasDefaultQuiz);

            if (quiz.isEmpty()) throw new ApiException(ApiErrorType.DONE_QUIZ);

            int randomIndex = (int)(Math.random() * quiz.size());
            return QuizMapper.INSTANCE.entityToResponse(quiz.get(randomIndex));
        } else {
            throw new ApiException(ApiErrorType.DONE_QUIZ);
        }
    }

    public QuizResponse getQuizWithSubjects(String userId, List<String> subject, boolean hasUserQuiz, boolean hasDefaultQuiz, boolean hasSolvedQuiz) {
        List<ActiveQuizEntity> allQuiz = new ArrayList<>();

        for (String sub : subject) {
            List<ActiveQuizEntity> quiz;

            // 푼 퀴즈를 나오게 설정한 경우 포함해서 검색
            if (hasSolvedQuiz) quiz = activeQuizRepository.getAnOpenQuizWithSolved(sub);
            else quiz = activeQuizRepository.getAnOpenQuiz(userId, sub);

            // 각 subject에 대해 검색된 퀴즈를 allQuiz에 추가
            if (quiz != null && !quiz.isEmpty()) {
                allQuiz.addAll(quiz);
            }
        }

        if (!allQuiz.isEmpty()) {
            // 필터링을 통해 사용자 문제와 기본 문제 설정에 따라 제거
            allQuiz = quizFiltering(allQuiz, hasUserQuiz, hasDefaultQuiz);

            if (allQuiz.isEmpty()) throw new ApiException(ApiErrorType.DONE_QUIZ);

            int randomIndex = (int)(Math.random() * allQuiz.size());
            return QuizMapper.INSTANCE.entityToResponse(allQuiz.get(randomIndex));
        } else {
            throw new ApiException(ApiErrorType.DONE_QUIZ);
        }
    }

    public List<ActiveQuizEntity> quizFiltering(List<ActiveQuizEntity> quiz, boolean hasUserQuiz, boolean hasDefaultQuiz) {
        if (!hasUserQuiz) {
            quiz.removeIf(q -> userQuizRepository.findById(q.getQuizId()).isPresent());
        }
        if (!hasDefaultQuiz) {
            quiz.removeIf(q -> defaultQuizRepository.findById(q.getQuizId()).isPresent());
        }

        return quiz;
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

            log.info("User solved quiz [quizId: {}, userId{}]", quizId, userId);
            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            // 잘못된 quiz id를 전달했을 때 발생하는 제약 조건 위반 예외
            throw new ApiException(ApiErrorType.NOT_FOUND_ID);
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

    public ResponseEntity<?> getQuizImage(Long quizId) {
        try {
            BufferedImage image = ImageIO.read(new File(imgPath + "/" + quizId +".jpg"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(image, "jpg", baos);
            byte[] imageData = baos.toByteArray();

            String base64Image = Base64.getEncoder().encodeToString(imageData);

            return ResponseEntity.ok().body(base64Image);
        } catch (IOException e) {
            log.error(e.toString());
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR);
        }
    }
}
