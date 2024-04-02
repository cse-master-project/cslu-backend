package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.QuizRejectDTO;
import com.example.csemaster.entity.QuizRejectEntity;
import com.example.csemaster.dto.UnApprovalQuizDTO;
import com.example.csemaster.mapper.QuizRejectMapper;
import com.example.csemaster.repository.QuizRejectRepository;
import com.example.csemaster.repository.UserQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalQuizService {
    private final UserQuizRepository userQuizRepository;
    private final QuizRejectRepository quizRejectRepository;
    private final QuizRejectMapper quizRejectMapper;

    public List<UnApprovalQuizDTO> getUnApprovalQuiz() {
        return userQuizRepository.getAnApprovalQuiz();
    }

    public ResponseEntity<?> setQuizPermission(Long quizId, Integer state) {
        return userQuizRepository.findById(quizId)
                .map(quiz -> {
                    quiz.setPermissionStatus(state);
                    userQuizRepository.save(quiz);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> setQuizRejection(QuizRejectDTO quizRejectDTO, Integer state) {
        QuizRejectEntity quizReject = quizRejectMapper.toQuizRejectEntity(quizRejectDTO);
        quizRejectRepository.save(quizReject);
        return userQuizRepository.findById(quizRejectDTO.getQuizId())
                .map(quiz -> {
                    quiz.setPermissionStatus(state);
                    userQuizRepository.save(quiz);
                    return ResponseEntity.ok().build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
