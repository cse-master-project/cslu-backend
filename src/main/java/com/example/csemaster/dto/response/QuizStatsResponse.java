package com.example.csemaster.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
public class QuizStatsResponse {
    private Double totalCorrectRate;
    private Map<String, Double> correctRateBySubject;
}
