package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.QuizDTO;
import com.example.csemaster.entity.*;
import com.example.csemaster.features.login.user.UserLoginService;
import com.example.csemaster.jwt.JwtProvider;
import com.example.csemaster.mapper.AddQuizMapper;
import com.example.csemaster.repository.DefaultQuizRepository;
import com.example.csemaster.repository.ManagerRepository;
import com.example.csemaster.repository.QuizRepository;
import com.example.csemaster.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizCreateService {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AddQuizMapper addQuizMapper;
    private final QuizRepository quizRepository;
    private final ManagerRepository managerRepository;
    private final DefaultQuizRepository defaultQuizRepository;
    private final UserLoginService userLoginService;
    private final UserRepository userRepository;

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
                Set<String> answerTypes = Set.of("1", "2", "3", "4");

                // 'answer' 필드가 1~4 값을 가졌는지 확인
                if (!answerTypes.contains(answerValue)) {
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

    public Long addQuiz(QuizDTO quizDTO) {
        // jsonContent 형식 검사
        if (!isValidJsonContent(quizDTO.getJsonContent())) {
            throw new RuntimeException("Incorrect jsonContent");
        }

        // Quiz 테이블에 추가
        QuizEntity quizEntity = addQuizMapper.toQuizEntity(quizDTO);
        quizRepository.save(quizEntity);

        log.info("Quiz 저장 완료");

        return quizEntity.getQuizId();
    }

    public Boolean addDefaultQuiz(Long quizId, String managerId) {
        // managerId를 사용하여 ManagerEntity 조회
        Optional<ManagerEntity> managerEntityOptional = managerRepository.findById(managerId);
        if (!managerEntityOptional.isPresent()) {
            throw new RuntimeException("Manager not found with id: " + managerId);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (!quizEntity.isPresent()) {
            throw new RuntimeException("Incorrect quizId");
        }

        // Default Quiz 테이블에 추가
        DefaultQuizEntity defaultQuizEntity = new DefaultQuizEntity();
        defaultQuizEntity.setDefaultQuizId(quizId);
        defaultQuizEntity.setManagerId(managerEntityOptional.get());

        defaultQuizRepository.save(defaultQuizEntity);

        log.info("Default Quiz 저장 완료");
        return true;
    }

    public Boolean addUserQuiz(Long quizId, String userId) {
        // userId를 사용하여 UserEntity 조회
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (!userEntity.isPresent()) {
            throw new RuntimeException("Manager not found with id: " + quizId);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (!quizEntity.isPresent()) {
            throw new RuntimeException("Incorrect quizId");
        }

        // User Quiz 테이블에 추가
        UserQuizEntity userQuizEntity = new UserQuizEntity();
        userQuizEntity.setUserQuizId(quizId);
        userQuizEntity.setPermissionStatus(false);
        userQuizEntity.setUserId(userEntity.get());

        return true;
    }

    @Transactional
    public Boolean addQuizAndDefaultQuiz(QuizDTO quizDTO, String managerId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 DefaultQuiz 추가
        return addDefaultQuiz(quizId, managerId);
    }

    @Transactional
    public Boolean addQuizAndUserQuiz(QuizDTO quizDTO, String userId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 UserQuiz 추가
        return addUserQuiz(quizId, userId);
    }


}
