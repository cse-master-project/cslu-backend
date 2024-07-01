package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.QuizDTO;
import com.example.csemaster.entity.*;
import com.example.csemaster.exception.CustomException;
import com.example.csemaster.exception.ExceptionEnum;
import com.example.csemaster.mapper.AddQuizMapper;
import com.example.csemaster.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
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

    @Value("${img.file.path}")
    private String imgPath;

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
                throw new CustomException(ExceptionEnum.NULL_VALUE);
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
                // 'answer' 필드가 빈 문자열이 아닌지 확인
                if (answerValue.isEmpty()) {
                    log.debug("answer 필드가 비어있음");
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
                log.debug("answer 필드가 배열이 아니거나 비어있음");
                return answerNode.isArray() && !answerNode.isEmpty();
            }

            return true;
        } catch (JsonProcessingException e) {
            throw new CustomException(ExceptionEnum.INVALID_JSON);
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

    public void addDefaultQuiz(Long quizId, String managerId) {
        // managerId를 사용하여 ManagerEntity 조회
        Optional<ManagerEntity> managerEntityOptional = managerRepository.findById(managerId);
        if (managerEntityOptional.isEmpty()) {
            throw new CustomException(ExceptionEnum.INVALID_IDENTIFIER);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (quizEntity.isEmpty()) {
            // 무결성 문제라서 500 반환
            log.debug("존재하지 않는 문제");
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }

        // Default Quiz 테이블에 추가
        DefaultQuizEntity defaultQuizEntity = new DefaultQuizEntity();
        defaultQuizEntity.setDefaultQuizId(quizId);
        defaultQuizEntity.setManagerId(managerEntityOptional.get());

        defaultQuizRepository.save(defaultQuizEntity);

        log.info("Default Quiz 저장 완료");
    }

    public void addUserQuiz(Long quizId, String userId) {
        // userId를 사용하여 UserEntity 조회
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isEmpty()) {
            throw new CustomException(ExceptionEnum.INVALID_IDENTIFIER);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (quizEntity.isEmpty()) {
            log.debug("존재하지 안는 문제");
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }

        // User Quiz 테이블에 추가
        UserQuizEntity userQuizEntity = new UserQuizEntity();
        userQuizEntity.setUserQuizId(quizId);
        userQuizEntity.setPermissionStatus(0);
        userQuizEntity.setUserId(userEntity.get());

        userQuizRepository.save(userQuizEntity);
        log.info("User Quiz 저장 완료");
    }

    @Transactional
    public Long addQuizAndDefaultQuiz(QuizDTO quizDTO, String managerId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 DefaultQuiz 추가
        addDefaultQuiz(quizId, managerId);

        log.info("Quiz has been saved successfully");
        return quizId;
    }

    @Transactional
    public Long addQuizAndUserQuiz(QuizDTO quizDTO, String userId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 UserQuiz 추가
        addUserQuiz(quizId, userId);

        log.info("Quiz has been saved successfully");
        return quizId;
    }

    private String getFileExtension(String[] strings, Long quizId) {
        System.out.println(strings[0].toLowerCase());
        if (strings[0].toLowerCase().contains("png")) {
            return quizId.toString() + ".png";
        } else if (strings[0].toLowerCase().contains("jpeg") || strings[0].toLowerCase().contains("jpg")) {
            return quizId.toString() + ".jpg";
        } else {
            throw new CustomException(ExceptionEnum.UNSUPPORTED_FILE_EXTENSION);
        }
    }

    private void saveImage(Long quizId, String base64String) {
        try {
            String[] strings = base64String.split(",");
            String filename = imgPath + "/" + quizId + ".jpg";  // 무조건 jpg 로 저장

            File directory = new File(imgPath);
            if (!directory.exists()) {
                directory.mkdirs(); // 폴더가 존재하지 않는다면 생성
            }

            byte[] decodedBytes = Base64.getDecoder().decode(strings[1]);
            System.out.println(filename);
            FileOutputStream fos = new FileOutputStream(filename, false);
            fos.write(decodedBytes);
            fos.close();

            log.info("file save successful. [quizId: " + quizId + "]");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            // 입출력 실패시 500
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void userUploadImage(String userId, Long quizId, String base64String) {
        Optional<UserQuizEntity> userQuiz = userQuizRepository.findByQuizIdAndUserId(quizId, userId);
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);
        if (userQuiz.isPresent() && quiz.isPresent()) {
            if (quiz.get().getHasImage().equals(false)) {
                saveImage(quizId, base64String);

                userQuiz.get().getQuiz().setHasImage(true);
                quizRepository.save(userQuiz.get().getQuiz());
            } else {
                log.debug("이미 이미지가 존재하는 quiz ID");
                throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.debug("존재하지 않는 quiz ID");
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public void managerUploadImage(String managerId, Long quizId, String base64String) {
        Optional<DefaultQuizEntity> defaultQuiz = defaultQuizRepository.findByQuizIdAndManagerId(quizId, managerId);
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);
        if (defaultQuiz.isPresent() && quiz.isPresent()) {
            if (quiz.get().getHasImage().equals(false)) {
                saveImage(quizId, base64String);

                defaultQuiz.get().getQuiz().setHasImage(true);
                quizRepository.save(defaultQuiz.get().getQuiz());
            } else {
                log.debug("이미 이미지가 존재하는 quiz ID");
                throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
            }
        } else {
            log.debug("존재하지 않는 quiz ID");
            throw new CustomException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }
}
