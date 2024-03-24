package com.example.csemaster.features.default_quiz;

import com.example.csemaster.features.login.manager.ManagerEntity;
import com.example.csemaster.features.login.manager.ManagerRepository;
import com.example.csemaster.features.quiz.QuizDTO;
import com.example.csemaster.features.quiz.QuizEntity;
import com.example.csemaster.jwt.JwtProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultQuizService {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AddQuizMapper addQuizMapper;
    private final QuizRepository quizRepository;
    private final ManagerRepository managerRepository;
    private final DefaultQuizRepository defaultQuizRepository;

    // jsonContent 형식 검사
    public boolean isValidJsonContent(String jsonContent) {
        try {
            // 전체 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            /* 1. 4지선다 / 2. 단답식 / 3. 선 긋기 / 4. O/X / 5. 빈칸 채우기 */
            Set<String> allowedTypes = Set.of("1", "2", "3", "4", "5");

            String typeValue = rootNode.path("type").asText();
            String quizValue = rootNode.path("quiz").asText();
            JsonNode answerNode = rootNode.path("answer");
            String answerValue = rootNode.path("answer").asText();
            JsonNode optionNode = rootNode.path("option");
            JsonNode leftOptionNode = rootNode.path("left_option");
            JsonNode rightOptionNode = rootNode.path("right_option");

            // 'type' 필드가 1~5 값을 가졌는지 확인
            if (!allowedTypes.contains(typeValue)) {
                return false;
            }

            // 'quiz' 필드가 빈 문자열이 아닌지 확인
            if (quizValue.isEmpty()) {
                return false;
            }

            // 1. 4지선다
            if ("1".equals(typeValue)) {
                // 'answer' 필드의 길이가 1인지 확인
                if (answerValue.length() != 1) {
                    return false;
                }

                // 'option' 필드가 배열이며, 크기가 4인지 확인
                if (!optionNode.isArray() || optionNode.size() != 4) {
                    return false;
                }
            }

            // 2. 단답식
            if ("2".equals(typeValue)) {
                // 'answer' 필드가 빈 문자열이 아닌지 확인
                if (answerValue.isEmpty()) {
                    return false;
                }
            }

            // 3. 선 긋기
            if ("3".equals(typeValue)) {
                // 'left_option' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!leftOptionNode.isArray() || leftOptionNode.isEmpty()) {
                    return false;
                }

                // 'right_option' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!rightOptionNode.isArray() || rightOptionNode.isEmpty()) {
                    return false;
                }

                // 'answer' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!answerNode.isArray() || answerNode.isEmpty()) {
                    return false;
                }
            }

            // 4. O/X
            if ("4".equals(typeValue)) {
                Set<String> zeroOrOne = Set.of("0", "1");

                // 'answer' 필드가 0, 1만 가지고 있는지 확인
                if (!zeroOrOne.contains(answerValue)) {
                    return false;
                }
            }

            // 5. 빈칸 채우기
            if ("5".equals(typeValue)) {
                // 'answer' 필드가 배열이며, 빈 배열이 아닌지 확인
                if (!answerNode.isArray() || answerNode.isEmpty()) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public Boolean addQuiz(String token, QuizDTO quizDTO) {
        System.out.println("이거 맞냐고 " + isValidJsonContent(quizDTO.getJsonContent()));

        // jsonContent 형식 검사
        if (!isValidJsonContent(quizDTO.getJsonContent())) {
            throw new RuntimeException("Incorrect jsonContent");
        }

        // Quiz 테이블에 추가
        QuizEntity quizEntity = addQuizMapper.toQuizEntity(quizDTO);
        quizRepository.save(quizEntity);

        log.info("Quiz 저장 완료");

        // 저장한 quiz의 id 추출
        Long savedQuizId = quizEntity.getQuizId();

        // Default Quiz 메서드 호출
        Boolean defaultQuiz = addDefaultQuiz(savedQuizId, token);
        if (!defaultQuiz) {
            throw new RuntimeException("Failed - addDefaultQuiz()");
        }

        return true;
    }

    public Boolean addDefaultQuiz(Long quizId, String token) {
        // 토큰 검증
        if (!jwtProvider.validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        // 토큰으로부터 사용자 정보 추출
        Authentication authentication = jwtProvider.getAuthentication(token);
        String managerId = authentication.getName();

        // managerId를 사용하여 ManagerEntity 조회
        Optional<ManagerEntity> managerEntityOptional = managerRepository.findById(managerId);
        if (!managerEntityOptional.isPresent()) {
            throw new RuntimeException("Manager not found with id: " + managerId);
        }

        // Default Quiz 테이블에 추가
        DefaultQuizEntity defaultQuizEntity = new DefaultQuizEntity();
        defaultQuizEntity.setDefaultQuizId(quizId);
        defaultQuizEntity.setManagerId(managerEntityOptional.get());

        defaultQuizRepository.save(defaultQuizEntity);

        log.info("Default Quiz 저장 완료");
        return true;
    }
}
