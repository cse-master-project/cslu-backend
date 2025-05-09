package com.example.csemaster.v2.domain.quiz.management;

import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.repository.QuizRepository;
import com.example.csemaster.v2.dto.UnApprovalQuizDTO;
import com.example.csemaster.core.dao.quiz.accessory.QuizRejectEntity;
import com.example.csemaster.core.repository.QuizRejectRepository;
import com.example.csemaster.core.repository.UserQuizRepository;
import com.example.csemaster.v2.dto.response.QuizRejectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "V2ApprovalQuizService")
@RequiredArgsConstructor
@Slf4j
public class ApproveService {
    private final UserQuizRepository userQuizRepository;
    private final QuizRejectRepository quizRejectRepository;
    private final QuizRepository quizRepository;

    // 미승인 문제 조회 (페이징)
    public Page<UnApprovalQuizDTO> getUnApprovalQuiz(Pageable pageable) {
        return userQuizRepository.getAnApprovalQuiz(pageable);
    }

    // 문제 승인
    public ResponseEntity<?> setQuizPermission(Long quizId, Integer state) {
        return userQuizRepository.findById(quizId)
                .map(quiz -> {
                    quiz.setPermissionStatus(state);
                    userQuizRepository.save(quiz);
                    log.info("User quiz approved [quizId: {}]", quizId);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    // 문제 반려
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
