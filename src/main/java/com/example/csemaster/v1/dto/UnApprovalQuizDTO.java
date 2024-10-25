package com.example.csemaster.v1.dto;

import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UnApprovalQuizDTO {
    private Long quizId;
    private String subject;
    private String detailSubject;
    private Integer quizType;
    private String jsonContent;
    private LocalDateTime createAt;
    private String userNickname;

    public UnApprovalQuizDTO(QuizEntity quiz, String nickname) {
        this.quizId = quiz.getQuizId();
        this.subject = quiz.getSubject();
        this.detailSubject = quiz.getChapter();
        this.quizType = quiz.getQuizType();
        this.jsonContent = quiz.getJsonContent();
        this.createAt = quiz.getCreateAt();
        this.userNickname = nickname;
    }
}
