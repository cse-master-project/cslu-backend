package com.example.csemaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "default_quiz")
public class DefaultQuizEntity {
    @Id
    @Column(name = "default_quiz_id")
    private Long defaultQuizId;

    @OneToOne
    @JoinColumn(name = "default_quiz_id", referencedColumnName = "quiz_id")
    private QuizEntity quiz;

    @OneToOne
    @JoinColumn(name = "manager_id_for_default_quiz")
    private ManagerEntity managerId;
}
