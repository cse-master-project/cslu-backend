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
    // jsonContent 형식 검사
    public static void isValidJsonContent(Integer quizType, String jsonContent) {
        try {
            QuizContent quizContent = new QuizContent(jsonContent);

            // 'quiz' 또는 'commentary' 필드가 비어있는지 확인
            if (quizContent.quizValue.isEmpty() || quizContent.commentaryValue.isEmpty()) {
                log.debug("[quiz, commentary] 빈 문자열");
                throw new ApiException(ApiErrorType.NULL_VALUE, "[quiz, commentary] 빈 문자열");
            }

            switch (quizType) {
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
        String optionValue;
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
            this.optionValue = rootNode.path("option").asText();
            this.leftOptionNode = rootNode.path("left_option");
            this.rightOptionNode = rootNode.path("right_option");
        }
    }

    private static void isValidMultipleChoice(QuizContent quizContent) {
        // 'answer' 필드가 1~4 값을 가졌는지 확인
        if (!Set.of("1", "2", "3", "4").contains(quizContent.answerValue)) {
            log.debug("[answer] 1~4 이외의 값을 가짐");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 1~4 이외의 값을 가짐");
        }

        // 'option' 필드가 배열이며, 크기가 4인지 확인
        if (!quizContent.optionNode.isArray() || quizContent.optionNode.size() != 4) {
            log.debug("[option] 배열이 아니거나, 보기 4개가 아님");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[option] 배열이 아니거나, 보기 4개가 아님");
        }

        // option 필드의 내용 검사
        for (int i = 0; i < quizContent.optionNode.size(); i++) {
            if (quizContent.optionNode.get(i).asText().isEmpty()) {
                log.debug("[option] 빈 문자열 요소가 있음");
                throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[option] 빈 문자열 요소가 있음");
            }
        }
    }

    private static void isValidShortAnswer(QuizContent quizContent) throws JsonProcessingException {
        // 'answer' 필드가 배열인지 비어있는지 확인
        if (!quizContent.answerNode.isArray() || quizContent.answerNode.isEmpty()) {
            log.debug("[answer] 배열이 아니거나, 빈 배열");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 배열이 아니거나, 빈 배열");
        }
    }

    private static void isValidDrawLine(QuizContent quizContent) throws JsonProcessingException {
        // 'left_option' 필드가 배열이며, 빈 배열이 아닌지 확인
        if (!quizContent.leftOptionNode.isArray() || quizContent.leftOptionNode.isEmpty()) {
            log.debug("[left_option] 배열이 아니거나, 빈 배열");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[left_option] 배열이 아니거나, 빈 배열");
        }

        // 'right_option' 필드가 배열이며, 빈 배열이 아닌지 확인
        if (!quizContent.rightOptionNode.isArray() || quizContent.rightOptionNode.isEmpty()) {
            log.debug("[right_option] 배열이 아니거나, 빈 배열");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[right_option] 배열이 아니거나, 빈 배열");
        }

        // 'answer' 필드가 배열이며, 빈 배열이 아닌지 확인
        if (!quizContent.answerNode.isArray() || quizContent.answerNode.isEmpty()) {
            log.debug("[answer] 배열이 아니거나, 빈 배열");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 배열이 아니거나, 빈 배열");
        }
    }

    private static void isValidOXQuiz(QuizContent quizContent) throws JsonProcessingException {
        // 'answer' 필드가 0, 1만 가지고 있는지 확인
        if (!Set.of("0", "1").contains(quizContent.answerValue)) {
            log.debug("[answer] 0, 1 이외의 값을 가짐");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 0, 1 이외의 값을 가짐");
        }
    }

    private static void isValidFillBlank(QuizContent quizContent) {
        if (!quizContent.answerNode.isArray() || quizContent.answerNode.isEmpty()) {
            log.debug("[answer] 배열이 아니거나, 빈 배열");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 배열이 아니거나, 빈 배열");
        }

        // 'answer' 필드의 요소가 세 개 이하인지 확인
        if (quizContent.answerNode.size() > 3) {
            log.debug("[answer] 답안이 3개를 초과함");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 답안이 3개를 초과함");
        }

        // answer 필드의 내용 검사
        for (int i = 0; i < quizContent.answerNode.size(); i++) {
            if (!quizContent.answerNode.get(i).isArray() || quizContent.answerNode.get(i).isEmpty()) {
                log.debug("[answer] 배열이 아닌 요소나, 빈 배열 요소가 있음");
                throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 배열이 아닌 요소나, 빈 배열 요소가 있음");
            }
        }

        // answer 필드의 요소 개수 검사
        if (blankCnt(quizContent.quizValue) != quizContent.answerNode.size()) {
            log.debug("[answer] 빈칸과 답안의 개수가 일치하지 않음");
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT, "[answer] 빈칸과 답안의 개수가 일치하지 않음");
        }
    }

    // 빈칸 개수
    private static int blankCnt(String text) {
        int count = 0;
        int index = 0;

        while ((index = text.indexOf("<<빈칸>>", index)) != -1) {
            count++;
            index += "<<빈칸>>".length();
        }
        return count;
    }
}
