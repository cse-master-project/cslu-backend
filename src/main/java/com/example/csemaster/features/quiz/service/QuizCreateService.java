package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.QuizDTO;
import com.example.csemaster.entity.*;
import com.example.csemaster.mapper.AddQuizMapper;
import com.example.csemaster.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizCreateService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AddQuizMapper addQuizMapper;
    private final QuizRepository quizRepository;
    private final ManagerRepository managerRepository;
    private final DefaultQuizRepository defaultQuizRepository;
    private final UserRepository userRepository;
    private final UserQuizRepository userQuizRepository;
    private final QuizSubjectRepository quizSubjectRepository;
    private final QuizDetailSubjectRepository quizDetailSubjectRepository;

    // jsonContent 형식 검사
    public boolean isValidJsonContent(Integer quizType, String jsonContent) {
        try {
            /* 1. 4지선다 / 2. 단답식 / 3. 선 긋기 / 4. O/X / 5. 빈칸 채우기 */

            // 전체 JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            String typeValue = rootNode.path("type").asText();
            String quizValue = rootNode.path("quiz").asText();
            String commentaryValue = rootNode.path("commentary").asText();
            JsonNode answerNode = rootNode.path("answer");
            String answerValue = rootNode.path("answer").asText();
            JsonNode optionNode = rootNode.path("option");
            JsonNode leftOptionNode = rootNode.path("left_option");
            JsonNode rightOptionNode = rootNode.path("right_option");

            // 'quiz' 필드가 빈 문자열이 아닌지 확인
            if (quizValue.isEmpty()) {
                return false;
            }

            // 'commentary' 필드가 빈 문자열이 아닌지 확인
            if (commentaryValue.isEmpty()) {
                return false;
            }

            // 1. 4지선다
            if (quizType.equals(1)) {
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
            if (quizType.equals(2)) {
                // 'answer' 필드가 빈 문자열이 아닌지 확인
                if (answerValue.isEmpty()) {
                    return false;
                }
            }

            // 3. 선 긋기
            if (quizType.equals(3)) {
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
            if (quizType.equals(4)) {
                Set<String> zeroOrOne = Set.of("0", "1");

                // 'answer' 필드가 0, 1만 가지고 있는지 확인
                if (!zeroOrOne.contains(answerValue)) {
                    return false;
                }
            }

            // 5. 빈칸 채우기
            if (quizType.equals(5)) {
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
        // subject, detailSubject 확인
        Optional<SubjectEntity> subject = quizSubjectRepository.findBySubject(quizDTO.getSubject());
        if (subject.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_SUBJECT);
        }

        Optional<DetailSubjectEntity> detailSubject = quizDetailSubjectRepository.findBySubjectIdAndDetailSubject(subject.get().getSubjectId(), quizDTO.getDetailSubject());
        if (detailSubject.isEmpty()) {
            throw new CustomException(ExceptionEnum.NOT_FOUND_DETAIL_SUBJECT);
        }

        // jsonContent 형식 검사
        if (!isValidJsonContent(quizDTO.getQuizType(), quizDTO.getJsonContent())) {
            throw new CustomException(ExceptionEnum.INCORRECT_QUIZ_CONTENT);
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
        if (managerEntityOptional.isEmpty()) {
            throw new CustomException(ExceptionEnum.INVALID_IDENTIFIER);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (quizEntity.isEmpty()) {
            // 무결성 문제라서 500 반환
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
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
        if (userEntity.isEmpty()) {
            throw new CustomException(ExceptionEnum.INVALID_IDENTIFIER);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (quizEntity.isEmpty()) {
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }

        // User Quiz 테이블에 추가
        UserQuizEntity userQuizEntity = new UserQuizEntity();
        userQuizEntity.setUserQuizId(quizId);
        userQuizEntity.setPermissionStatus(0);
        userQuizEntity.setUserId(userEntity.get());

        userQuizRepository.save(userQuizEntity);

        return true;
    }

    @Transactional
    public Long addQuizAndDefaultQuiz(QuizDTO quizDTO, String managerId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 DefaultQuiz 추가
        if (!addDefaultQuiz(quizId, managerId)) {
            throw new RuntimeException("QuizCreateService - addDefaultQuiz()");
        }

        log.info("Quiz has been saved successfully");
        return quizId;
    }

    @Transactional
    public Long addQuizAndUserQuiz(QuizDTO quizDTO, String userId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 UserQuiz 추가
        if (!addUserQuiz(quizId, userId)) {
            throw new RuntimeException("QuizCreateService - addUserQuiz()");
        }

        log.info("Quiz has been saved successfully");
        return quizId;
    }

    private String getFileExtension(String[] strings, Long quizId) {
        if (strings[0].toLowerCase().contains("png")) {
            return quizId.toString() + ".png";
        } else if (strings[0].toLowerCase().contains("jpeg") || strings[0].toLowerCase().contains("jpg")) {
            return quizId.toString() + ".jpg";
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 형식");
        }
    }

    private void saveImage(Long quizId, String base64String) throws IOException {
        String[] strings = base64String.split(",");
        String filename = getFileExtension(strings, quizId);

        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        FileOutputStream fos = new FileOutputStream(filename);
        fos.write(decodedBytes);
        fos.close();

        log.info("file save successful. [quizId: " + quizId + "]");
    }

    @Transactional
    public ResponseEntity<?> userUploadImage(String userId, Long quizId, String base64String) {
        return userQuizRepository.findByQuizIdAndUserId(quizId, userId).map(quiz -> {
            try {
                saveImage(quizId, base64String);
            } catch (IOException e) {
                return ResponseEntity.notFound().build();
            }
            quiz.getQuiz().setHasImage(true);
            quizRepository.save(quiz.getQuiz());

            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());

    }

    public ResponseEntity<?> managerUploadImage(String managerId, Long quizId, String base64String) {
        return defaultQuizRepository.findByQuizIdAndManagerId(quizId, managerId).map(quiz -> {
            try {
                saveImage(quizId, base64String);
            } catch (IOException e) {
                return ResponseEntity.notFound().build();
            }
            quiz.getQuiz().setHasImage(true);
            quizRepository.save(quiz.getQuiz());

            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
