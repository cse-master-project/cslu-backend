package com.example.csemaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "quiz_reject")
public class QuizRejectEntity {
    @Id
    @Column(name = "quiz_id_for_quiz_reject")
    private Long quizId;

    @OneToOne
    @JoinColumn(name = "quiz_id_for_quiz_reject", referencedColumnName = "quiz_id")
    private QuizEntity quiz;

    @Column
    private String reason;
}
