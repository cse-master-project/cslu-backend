package com.example.csemaster.core.tools;

import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class QuizValidator {
    // TODO : 문제 유형별로 함수로 나눠서 이 함수 줄이고, core 패키지로 옮기기
    // TODO : 검증에 실패한 이유를 프론트에 알려주기
    // TODO : 테스트 필요
    // jsonContent 형식 검사
    public static boolean isValidJsonContent(Integer quizType, String jsonContent) {
        try {
            QuizContent quizContent = new QuizContent(jsonContent);

            // 'quiz' 필드가 빈 문자열이 아닌지 확인
            if (quizContent.quizValue.isEmpty() || quizContent.commentaryValue.isEmpty()) {
                log.debug("quiz 또는 commentary 필드가 비어있음");
                throw new ApiException(ApiErrorType.NULL_VALUE);
            }

            return switch (quizType) {
                // 1. 4지선다
                case 1 -> isValidMultipleChoice(quizContent);
                // 2. 단답식
                case 2 -> isValidShortAnswer(quizContent);
                // 3. 선 긋기
                case 3 -> isValidDrawLine(quizContent);
                // 4. O/X
                case 4 -> isValidOXQuiz(quizContent);
                // 5. 빈칸 채우기
                case 5 -> isValidFillBlank(quizContent);
                default -> false;
            };
        } catch (JsonProcessingException e) {
            throw new ApiException(ApiErrorType.INVALID_JSON);
        }
    }

    private static class QuizContent {
        String quizValue;
        String commentaryValue;
        JsonNode answerNode;
        String answerValue;
        JsonNode optionNode;
        JsonNode leftOptionNode;
        JsonNode rightOptionNode;

        QuizContent(String jsonContent) throws JsonProcessingException {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            this.quizValue = rootNode.path("quiz").asText();
            this.commentaryValue = rootNode.path("commentary").asText();
            this.answerNode = rootNode.path("answer");
            this.answerValue = rootNode.path("answer").asText();
            this.optionNode = rootNode.path("option");
            this.leftOptionNode = rootNode.path("left_option");
            this.rightOptionNode = rootNode.path("right_option");
        }
    }

    private static boolean isValidMultipleChoice(QuizContent quizContent) {
        // 'answer' 필드가 1~4 값을 가졌는지 확인
        if (!Set.of("1", "2", "3", "4").contains(quizContent.answerValue)) {
            log.debug("answer 필드가 1~4 이외의 값을 가짐");
            return false;
        }

        // 'option' 필드가 배열이며, 크기가 4인지 확인
        if (!quizContent.optionNode.isArray() || quizContent.optionNode.size() != 4) {
            log.debug("option 필드가 배열이 아니거나 크기가 4가 아님");
            return false;
        }

        return true;
    }

    private static boolean isValidShortAnswer(QuizContent quizContent) throws JsonProcessingException {
        // 'answer' 필드가 배열인지 비어있는지 확인
        if (!quizContent.answerNode.isArray() || quizContent.answerNode.isEmpty()) {
            log.debug("answer 필드가 배열이 아니거나 비어있음");
            return false;
        }

        return true;
    }

    private static boolean isValidDrawLine(QuizContent quizContent) throws JsonProcessingException {
        // 'left_option' 필드가 배열이며, 빈 배열이 아닌지 확인
        if (!quizContent.leftOptionNode.isArray() || quizContent.leftOptionNode.isEmpty()) {
            log.debug("left_option 필드가 배열이 아니거나 비어있음");
            return false;
        }

        // 'right_option' 필드가 배열이며, 빈 배열이 아닌지 확인
        if (!quizContent.rightOptionNode.isArray() || quizContent.rightOptionNode.isEmpty()) {
            log.debug("right_option 필드가 배열이 아니거나 비어있음");
            return false;
        }

        // 'answer' 필드가 배열이며, 빈 배열이 아닌지 확인
        if (!quizContent.answerNode.isArray() || quizContent.answerNode.isEmpty()) {
            log.debug("answer 필드가 배열이 아니거나 비어있음");
            return false;
        }

        return true;
    }

    private static boolean isValidOXQuiz(QuizContent quizContent) throws JsonProcessingException {
        // 'answer' 필드가 0, 1만 가지고 있는지 확인
        if (!Set.of("0", "1").contains(quizContent.answerValue)) {
            log.debug("answer 필드가 0, 1 이외의 값을 가짐");
            return false;
        }

        return true;
    }

    private static boolean isValidFillBlank(QuizContent quizContent) {
        if (!quizContent.answerNode.isArray() && quizContent.answerNode.isEmpty()) {
            log.debug("answer 필드가 배열이 아니거나 비어있음");
            return false;
        }

        // 'answer' 필드의 요소가 세 개 이하인지 확인
        if (quizContent.answerNode.size() > 3) {
            log.debug("answer 필드의 요소가 세 개를 초과함");
            return false;
        }

        // answer 필드의 내용 검사
        for (int i = 0; i < quizContent.answerNode.size(); i++) {
            if (!quizContent.answerNode.isArray() && quizContent.answerNode.isEmpty()) {
                log.debug("answer 중에 배열이 아니거나 비어있는 요소가 있음.");
                return false;
            }
        }

        int blackCnt = quizContent.quizValue.length() - quizContent.quizValue.replace("<<빈칸>>", "").length();
        if (blackCnt != quizContent.answerNode.size()) {
            log.debug("빈칸과 답의 개수가 맞지 않음");
            return false;
        }
        return true;
    }
}
