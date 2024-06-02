package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.response.QuizResponse;
import com.example.csemaster.entity.*;
import com.example.csemaster.exception.CustomException;
import com.example.csemaster.exception.ExceptionEnum;
import com.example.csemaster.mapper.QuizMapper;
import com.example.csemaster.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizSolverService {
    private final ActiveQuizRepository activeQuizRepository;
    private final QuizLogRepository quizLogRepository;
    private final QuizReportRepository quizReportRepository;
    private final QuizSubjectRepository quizSubjectRepository;

    public QuizResponse getQuiz(String userId, String subject, List<String> detailSubject) {
        // 유저가 요청한 subject 와 detailSubject 가 유효한지 검증
        Optional<SubjectEntity> subjectEntity = quizSubjectRepository.findBySubject(subject);
        if (subjectEntity.isPresent()) {
            List<String> dbDetailSubject = subjectEntity.get().getDetailSubjects().stream().map(DetailSubjectEntity::getDetailSubject).toList();

            // 합집합 후에도 db에 있는 내용과 같다면 요소의 개수가 같음
            // 개수가 서로 다르다면 유효하지 않은 detailSubject 가 있다는 의미
            Set<String> set = new HashSet<>(detailSubject);
            set.addAll(dbDetailSubject);

            if (set.size() != dbDetailSubject.size()) throw new CustomException(ExceptionEnum.NOT_FOUND_DETAIL_SUBJECT);
        } else {
            throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        }

        // 유저가 풀지 않은 문제이면서 지정한 과목과 목차에 해당하는 문제를 한 개 제공
        List<ActiveQuizEntity> quiz = activeQuizRepository.getAnOpenQuiz(userId, subject, detailSubject);
        if (!quiz.isEmpty()) {
            int randomIndex = (int)(Math.random() * quiz.size());
            return QuizMapper.INSTANCE.entityToResponse(quiz.get(randomIndex));
        } else {
            throw new CustomException(ExceptionEnum.DONE_QUIZ);
        }
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

    public ResponseEntity<?> getQuizImage(Long quizId) {
        try {
            BufferedImage image = ImageIO.read(new File("/quiz-img/", quizId +".jpg"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(image, "jpg", baos);
            byte[] imageData = baos.toByteArray();

            String base64Image = Base64.getEncoder().encodeToString(imageData);

            return ResponseEntity.ok().body(base64Image);
        } catch (IOException e) {
            log.error(e.toString());
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
