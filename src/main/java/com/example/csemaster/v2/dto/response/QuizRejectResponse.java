package com.example.csemaster.v2.dto.response;

import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.core.dao.quiz.accessory.QuizRejectEntity;
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
