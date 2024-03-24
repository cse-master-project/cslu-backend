package com.example.csemaster.features.default_quiz;

import com.example.csemaster.features.login.manager.ManagerEntity;
import com.example.csemaster.features.quiz.QuizEntity;
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
