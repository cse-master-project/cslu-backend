package com.example.csemaster.v1.domain.quiz.service;

import com.example.csemaster.v1.dto.UnApprovalQuizDTO;
import com.example.csemaster.core.dao.quiz.accessory.QuizRejectEntity;
import com.example.csemaster.core.repository.QuizRejectRepository;
import com.example.csemaster.core.repository.UserQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApprovalQuizService {
    private final UserQuizRepository userQuizRepository;
    private final QuizRejectRepository quizRejectRepository;

    public List<UnApprovalQuizDTO> getUnApprovalQuiz() {
        return userQuizRepository.getAnApprovalQuizV1();
    }

    public ResponseEntity<?> setQuizPermission(Long quizId, Integer state) {
        return userQuizRepository.findById(quizId)
                .map(quiz -> {
                    quiz.setPermissionStatus(state);
                    userQuizRepository.save(quiz);
                    log.info("User quiz approved [quizId: {}]", quizId);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> setQuizRejection(Long quizId, String reason, Integer state) {
        QuizRejectEntity quizReject = new QuizRejectEntity();
        quizReject.setQuizId(quizId);
        quizReject.setReason(reason);

        quizRejectRepository.save(quizReject);
        return userQuizRepository.findById(quizId)
                .map(quiz -> {
                    quiz.setPermissionStatus(state);
                    userQuizRepository.save(quiz);

                    log.info("User quiz rejected [quizId: {}]", quizId);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
