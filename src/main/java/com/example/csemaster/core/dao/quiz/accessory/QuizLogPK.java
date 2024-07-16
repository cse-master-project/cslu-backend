package com.example.csemaster.core.dao.quiz.accessory;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class QuizLogPK implements Serializable {
    @Column(name = "quiz_id_for_quiz_log")
    private Long quizId;

    @Column(name = "user_id_for_quiz_log")
    private String userId;

    @Column(name = "try_cnt")
    private int tryCnt;
}
