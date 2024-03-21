package com.example.csemaster.features.quiz;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "quiz")
public class QuizEntity {
    @Id
    @Column(name = "quiz_id")
    private Long quizId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "detail_subject")
    private String detailSubject;

    @Column(name = "quiz_correct_rate")
    private double correctRate;

    // 모든 형식의 json을 받게 map을 사용하였으나 차후 개선 필요
    @Column(name = "quiz_content")
    private Map<String, Object> jsonContent;

    @Column(name = "quiz_created_at")
    private LocalDateTime createAt;
}
