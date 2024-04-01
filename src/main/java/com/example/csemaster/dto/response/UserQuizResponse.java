package com.example.csemaster.dto.response;

import com.example.csemaster.entity.QuizEntity;
import com.example.csemaster.entity.UserEntity;
import com.example.csemaster.entity.UserQuizEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserQuizResponse {
    private Long quizId;
    private String subject;
    private String detailSubject;
    private String jsonContent;
    private LocalDateTime createAt;
    private Integer permissionStatus;

    public UserQuizResponse(QuizEntity quiz, UserQuizEntity user) {
        this.quizId = quiz.getQuizId();
        this.subject = quiz.getSubject();
        this.detailSubject = quiz.getDetailSubject();
        this.jsonContent = quiz.getJsonContent();
        this.createAt = quiz.getCreateAt();
        this.permissionStatus = user.getPermissionStatus();
    }
}
