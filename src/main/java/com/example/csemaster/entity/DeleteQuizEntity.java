package com.example.csemaster.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "deleted_quiz_view")
public class DeleteQuizEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "detail_subject")
    private String chapter;

    @Column(name = "quiz_type")
    private Integer quizType;

    @Column(name = "quiz_correct_rate")
    private double correctRate;

    @Column(name = "quiz_content", columnDefinition = "TEXT")
    private String jsonContent;

    @Column(name = "quiz_created_at")
    private LocalDateTime createAt;

    @Column(name = "has_image")
    private Boolean hasImage;
}
