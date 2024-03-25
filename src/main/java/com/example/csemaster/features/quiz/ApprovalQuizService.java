package com.example.csemaster.features.quiz;

import com.example.csemaster.repository.UserQuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalQuizService {
    private final UserQuizRepository userQuizRepository;
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
}
