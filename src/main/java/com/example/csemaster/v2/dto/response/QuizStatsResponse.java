package com.example.csemaster.v2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class QuizStatsResponse {
    private int totalSolved;
    private int totalCorrect;
    private int totalIncorrect;
    private Double totalCorrectRate;
    private Map<String, Double> correctRateBySubject;
}
