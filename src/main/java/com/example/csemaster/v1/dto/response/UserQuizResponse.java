package com.example.csemaster.v1.dto.response;

import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.core.dao.quiz.core.UserQuizEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuizResponse {
    private Long quizId;
    private String subject;
    private String detailSubject;
    private Integer quizType;
    private String jsonContent;
    private LocalDateTime createAt;
    private Integer permissionStatus;

    public UserQuizResponse(QuizEntity quiz, UserQuizEntity user) {
        this.quizId = quiz.getQuizId();
        this.subject = quiz.getSubject();
        this.detailSubject = quiz.getChapter();
        this.quizType = quiz.getQuizType();
        this.jsonContent = quiz.getJsonContent();
        this.createAt = quiz.getCreateAt();
        this.permissionStatus = user.getPermissionStatus();
    }
}
