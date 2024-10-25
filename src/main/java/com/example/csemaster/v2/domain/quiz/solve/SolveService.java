package com.example.csemaster.v2.domain.quiz.solve;

import com.example.csemaster.core.dao.quiz.accessory.QuizLogEntity;
import com.example.csemaster.core.dao.quiz.accessory.QuizReportEntity;
import com.example.csemaster.core.dao.quiz.core.ActiveQuizEntity;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.repository.*;
import com.example.csemaster.v2.dto.response.QuizResponse;
import com.example.csemaster.v2.mapper.QuizMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service(value = "V2SolverService")
@Slf4j
@RequiredArgsConstructor
public class SolveService {
    private final ActiveQuizRepository activeQuizRepository;
    private final QuizLogRepository quizLogRepository;
    private final QuizReportRepository quizReportRepository;
    private final UserQuizRepository userQuizRepository;
    private final DefaultQuizRepository defaultQuizRepository;

    // 문제 랜덤 조회
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

    // 문제 랜덤 조회 (과목 여러개)
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

    // 유저 문제, 기본 문제 구분해서 조건대로 필터링
    public List<ActiveQuizEntity> quizFiltering(List<ActiveQuizEntity> quiz, boolean hasUserQuiz, boolean hasDefaultQuiz) {
        if (!hasUserQuiz) {
            quiz.removeIf(q -> userQuizRepository.findById(q.getQuizId()).isPresent());
        }
        if (!hasDefaultQuiz) {
            quiz.removeIf(q -> defaultQuizRepository.findById(q.getQuizId()).isPresent());
        }

        return quiz;
    }

    // 문제 풀이 결과 저장
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

    // 문제 신고 저장
    public ResponseEntity<?> saveQuizReport(String userId, Long quizId, String content) {
        try {
            QuizReportEntity quizReport = new QuizReportEntity();

            quizReport.setQuizId(quizId);
            quizReport.setUserId(userId);
            quizReport.setContent(content);
            quizReport.setReportAt(LocalDateTime.now());
            quizReport.setIsProcessed(false);

            quizReportRepository.save(quizReport);

            return ResponseEntity.ok().build();
        } catch (DataIntegrityViolationException e) {
            // 잘못된 quiz id를 전달했을 때 발생하는 제약 조건 위반 예외
            return ResponseEntity.badRequest().body("Invalid value");
        }

    }


}
