package com.example.csemaster.v2.dto;

import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UnApprovalQuizDTO {
    private Long quizId;
    private String subject;
    private String chapter;
    private Integer quizType;
    private String jsonContent;
    private LocalDateTime createAt;
    private String creator;

    public UnApprovalQuizDTO(QuizEntity quiz, String creator) {
        this.quizId = quiz.getQuizId();
        this.subject = quiz.getSubject();
        this.chapter = quiz.getChapter();
        this.quizType = quiz.getQuizType();
        this.jsonContent = quiz.getJsonContent();
        this.createAt = quiz.getCreateAt();
        this.creator = creator;
    }
}
