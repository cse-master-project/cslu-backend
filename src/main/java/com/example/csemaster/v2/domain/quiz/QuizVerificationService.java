package com.example.csemaster.v2.domain.quiz;

import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service(value = "V2QuizVerificationService")
@Slf4j
public class QuizVerificationService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    // TODO : 문제 유형별로 함수로 나눠서 이 함수 줄이고, core 패키지로 옮기기
    // TODO : 검증에 실패한 이유를 프론트에 알려주기
    // jsonContent 형식 검사
    public boolean isValidJsonContent(Integer quizType, String jsonContent) {
        /* 1. 4지선다 / 2. 단답식 / 3. 선 긋기 / 4. O/X / 5. 빈칸 채우기 */

        try {
            // 전체 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            String quizValue = rootNode.path("quiz").asText();
            String commentaryValue = rootNode.path("commentary").asText();
            JsonNode answerNode = rootNode.path("answer");
            String answerValue = rootNode.path("answer").asText();
            JsonNode optionNode = rootNode.path("option");
            JsonNode leftOptionNode = rootNode.path("left_option");
            JsonNode rightOptionNode = rootNode.path("right_option");

            // 'quiz' 필드가 빈 문자열이 아닌지 확인
            if (quizValue.isEmpty() || commentaryValue.isEmpty()) {
                log.debug("quiz 또는 commentary 필드가 비어있음");
                throw new ApiException(ApiErrorType.NULL_VALUE);
            }

            // 1. 4지선다
            if (quizType.equals(1)) {
                Set<String> answerTypes = Set.of("1", "2", "3", "4");

                // 'answer' 필드가 1~4 값을 가졌는지 확인
                if (!answerTypes.contains(answerValue)) {
                    log.debug("answer 필드가 1~4 이외의 값을 가짐");
                    return false;
                }

                // 'option' 필드가 배열이며, 크기가 4인지 확인
                if (!optionNode.isArray() || optionNode.size() != 4) {
                    log.debug("option 필드가 배열이 아니거나 크기가 4가 아님");
                    return false;
                }
            }

            // 2. 단답식
            if (quizType.equals(2)) {
                // 'answer' 필드가 배열인지 비어있는지 확인
                if (!answerNode.isArray() || answerNode.isEmpty()) {
                    log.debug("answer 필드가 배열이 아니거나 비어있음");
                    return false;
                }
            }

            // 3. 선 긋기
            if (quizType.equals(3)) {
                // 'left_option' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!leftOptionNode.isArray() || leftOptionNode.isEmpty()) {
                    log.debug("left_option 필드가 배열이 아니거나 비어있음");
                    return false;
                }

                // 'right_option' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!rightOptionNode.isArray() || rightOptionNode.isEmpty()) {
                    log.debug("right_option 필드가 배열이 아니거나 비어있음");
                    return false;
                }

                // 'answer' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!answerNode.isArray() || answerNode.isEmpty()) {
                    log.debug("answer 필드가 배열이 아니거나 비어있음");
                    return false;
                }
            }

            // 4. O/X
            if (quizType.equals(4)) {
                Set<String> zeroOrOne = Set.of("0", "1");

                // 'answer' 필드가 0, 1만 가지고 있는지 확인
                if (!zeroOrOne.contains(answerValue)) {
                    log.debug("answer 필드가 0, 1 이외의 값을 가짐");
                    return false;
                }
            }

            // 5. 빈칸 채우기
            if (quizType.equals(5)) {
                // 'answer' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!answerNode.isArray() && answerNode.isEmpty()) {
                    log.debug("answer 필드가 배열이 아니거나 비어있음");
                    return false;
                }

                // 'answer' 필드의 요소가 세 개 이하인지 확인
                if (answerNode.size() > 3) {
                    log.debug("answer 필드의 요소가 세 개를 초과함");
                    return false;
                }

                // answer 필드의 내용 검사
                for (int i = 0; i < answerNode.size(); i++) {
                    if (!answerNode.isArray() && answerNode.isEmpty()) {
                        log.debug("answer 중에 배열이 아니거나 비어있는 요소가 있음.");
                        return false;
                    }
                }
            }

            return true;
        } catch (JsonProcessingException e) {
            throw new ApiException(ApiErrorType.INVALID_JSON);
        }
    }
}
