package com.example.csemaster.features.quiz.minyoung;

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
    private double correctRate;
    private String jsonContent;
    private LocalDateTime createAt;
    private Boolean hasImage;
    private Boolean isDeleted;
    private Long userQuizId;
    private Integer permissionStatus;

    public UserQuizResponse(QuizEntity quiz, UserQuizEntity user) {
        this.quizId = quiz.getQuizId();
        this.subject = quiz.getSubject();
        this.detailSubject = quiz.getDetailSubject();
        this.correctRate = quiz.getCorrectRate();
        this.jsonContent = quiz.getJsonContent();
        this.createAt = quiz.getCreateAt();
        this.hasImage = quiz.getHasImage();
        this.isDeleted = quiz.getIsDeleted();
        this.userQuizId = user.getUserQuizId();
        this.permissionStatus = user.getPermissionStatus();
    }
}
