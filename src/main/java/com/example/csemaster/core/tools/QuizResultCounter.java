package com.example.csemaster.core.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizResultCounter {
    // 과목별로 푼 횟수와 정답 횟수를 저장하는 Map (stats[0] : 푼 횟수, stats[1] : 정답 횟수)
    private Map<String, int[]> categoryStats;

    public QuizResultCounter(List<String> subjectList) {
        this.categoryStats = new HashMap<>();
        subjectList.forEach(subject -> this.categoryStats.put(subject, new int[]{0, 0}));
    }

    public void pushLog(String subject, boolean isCorrect) {
        int[] stats = this.categoryStats.get(subject);
        if (stats != null) {
            stats[0]++;
            stats[1] += isCorrect ? 1 : 0;
        } else {
            throw new IllegalArgumentException("Not Found Subject");
        }
    }

    public Map<String, Double> getCorrectRate() {
        Map<String, Double> resultMap = new HashMap<>();
        for (Map.Entry<String, int[]> entry : this.categoryStats.entrySet()) {
            int[] stats = entry.getValue();
            resultMap.put(entry.getKey(), ((double) stats[1] / stats[0]) * 100);
        }
        return resultMap;
    }

}
