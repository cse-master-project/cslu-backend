package com.example.csemaster.core.dao.quiz.accessory;

import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.core.dao.actor.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "quiz_report")
public class QuizReportEntity {
    @Id
    @Column(name = "quiz_report_id", columnDefinition = "INT UNSIGNED")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizReportId;

    @Column(name = "quiz_id_for_report")
    private Long quizId;

    @Column(name = "user_id_for_report")
    private String userId;

    @Column(name = "quiz_report_content")
    private String content;

    @Column(name = "quiz_report_at")
    private LocalDateTime reportAt;

    @Column(name = "is_processed")
    private Boolean isProcessed;

    @ManyToOne
    @JoinColumn(name="quiz_id_for_report", referencedColumnName = "quiz_id", insertable = false, updatable = false)
    private QuizEntity quiz;

    @ManyToOne
    @JoinColumn(name="user_id_for_report", referencedColumnName = "user_id", insertable = false, updatable = false)
    private UserEntity user;
}
