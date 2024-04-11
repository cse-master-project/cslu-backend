package com.example.csemaster.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "quiz_log")
@IdClass(QuizLogPK.class)
public class QuizLogEntity {
    @Id
    @Column(name = "quiz_id_for_quiz_log")
    private Long quizId;

    @Id
    @Column(name = "user_id_for_quiz_log")
    private String userId;

    @Id
    @Column(name = "try_cnt")
    private int tryCnt;


    @Column(name = "answer_status")
    private Boolean answerStatus;

    @Column(name = "result_created_at")
    private LocalDateTime solvedAt;


    @ManyToOne
    @JoinColumn(name="quiz_id_for_quiz_log", referencedColumnName = "quiz_id", insertable = false, updatable = false)
    private QuizEntity quiz;

    @ManyToOne
    @JoinColumn(name="user_id_for_quiz_log", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;
}


