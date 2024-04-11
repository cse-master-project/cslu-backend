package com.example.csemaster.dto.response;

import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.entity.QuizRejectEntity;
import lombok.Data;

@Data
public class QuizRejectResponse {
    private Long quizId;
    private String reason;

    public QuizRejectResponse(QuizEntity quiz, QuizRejectEntity quizReject) {
        this.quizId = quiz.getQuizId();
        this.reason = quizReject.getReason();
    }
}
