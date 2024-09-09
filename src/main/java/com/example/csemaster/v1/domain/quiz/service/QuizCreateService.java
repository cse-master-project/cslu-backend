package com.example.csemaster.v1.domain.quiz.service;

import com.example.csemaster.v1.dto.QuizDTO;
import com.example.csemaster.core.dao.actor.ManagerEntity;
import com.example.csemaster.core.dao.actor.UserEntity;
import com.example.csemaster.core.dao.quiz.category.ChapterEntity;
import com.example.csemaster.core.dao.quiz.category.SubjectEntity;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.dao.quiz.core.DefaultQuizEntity;
import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.core.dao.quiz.core.UserQuizEntity;
import com.example.csemaster.v1.mapper.AddQuizMapper;
import com.example.csemaster.core.repository.*;
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
    private final ChapterRepository quizDetailSubjectRepository;

    @Value("${img.file.path}")
    private String imgPath;

    // TODO : 문제 유형별로 함수로 나눠서 이 함수 줄이기
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

    public Long addQuiz(QuizDTO quizDTO) {
        // subject, detailSubject 확인
        Optional<SubjectEntity> subject = quizSubjectRepository.findBySubject(quizDTO.getSubject());
        if (subject.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        }

        Optional<ChapterEntity> detailSubject = quizDetailSubjectRepository.findBySubjectIdAndChapter(subject.get().getSubjectId(), quizDTO.getDetailSubject());
        if (detailSubject.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_DETAIL_SUBJECT);
        }

        // jsonContent 형식 검사
        if (!isValidJsonContent(quizDTO.getQuizType(), quizDTO.getJsonContent())) {
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT);
        }

        // Quiz 테이블에 추가
        QuizEntity quizEntity = addQuizMapper.toQuizEntity(quizDTO);
        quizRepository.save(quizEntity);

        // log.info("Quiz 저장 완료");

        return quizEntity.getQuizId();
    }

    public void addDefaultQuiz(Long quizId, String managerId) {
        // managerId를 사용하여 ManagerEntity 조회
        Optional<ManagerEntity> managerEntityOptional = managerRepository.findById(managerId);
        if (managerEntityOptional.isEmpty()) {
            throw new ApiException(ApiErrorType.INVALID_IDENTIFIER);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (quizEntity.isEmpty()) {
            // 무결성 문제라서 500 반환
            // log.debug("존재하지 않는 문제");
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR);
        }

        // Default Quiz 테이블에 추가
        DefaultQuizEntity defaultQuizEntity = new DefaultQuizEntity();
        defaultQuizEntity.setDefaultQuizId(quizId);
        defaultQuizEntity.setManagerId(managerEntityOptional.get());

        defaultQuizRepository.save(defaultQuizEntity);
    }

    public void addUserQuiz(Long quizId, String userId) {
        // userId를 사용하여 UserEntity 조회
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (userEntity.isEmpty()) {
            throw new ApiException(ApiErrorType.INVALID_IDENTIFIER);
        }

        // quizId가 존재하는지 확인
        Optional<QuizEntity> quizEntity = quizRepository.findByQuizId(quizId);
        if (quizEntity.isEmpty()) {
            // log.debug("존재하지 않는 문제");
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR);
        }

        // User Quiz 테이블에 추가
        UserQuizEntity userQuizEntity = new UserQuizEntity();
        userQuizEntity.setUserQuizId(quizId);
        userQuizEntity.setPermissionStatus(0);
        userQuizEntity.setUserId(userEntity.get());

        userQuizRepository.save(userQuizEntity);
        // log.info("User Quiz 저장 완료");
    }

    @Transactional
    public Long addQuizAndDefaultQuiz(QuizDTO quizDTO, String managerId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 DefaultQuiz 추가
        addDefaultQuiz(quizId, managerId);

        log.info("Create default quiz [new quizId: {}]", quizId);
        return quizId;
    }

    @Transactional
    public Long addQuizAndUserQuiz(QuizDTO quizDTO, String userId) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuiz(quizDTO);

        // 반환된 ID를 사용하여 UserQuiz 추가
        addUserQuiz(quizId, userId);

        log.info("Create user quiz [new quizId: {}, userId: {}]", quizId, userId);
        return quizId;
    }

    private String getFileExtension(String[] strings, Long quizId) {
        System.out.println(strings[0].toLowerCase());
        if (strings[0].toLowerCase().contains("png")) {
            return quizId.toString() + ".png";
        } else if (strings[0].toLowerCase().contains("jpeg") || strings[0].toLowerCase().contains("jpg")) {
            return quizId.toString() + ".jpg";
        } else {
            throw new ApiException(ApiErrorType.UNSUPPORTED_FILE_EXTENSION);
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

            // log.info("file save successful. [quizId: " + quizId + "]");
        } catch (IOException e) {
            // 입출력 실패시 500
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR, "image save error");
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
                log.info("Quiz image upload [quizId: {}, userId: {}]", quizId, userId);
            } else {
                // log.debug("이미 이미지가 존재하는 quiz ID");
                throw new ApiException(ApiErrorType.DUPLICATE_IMAGE);
            }
        } else {
            if (quiz.isEmpty()) throw new ApiException(ApiErrorType.NOT_FOUND_ID);  // 존재하지 않는 quiz id
            else throw new ApiException(ApiErrorType.ACCESS_DENIED_EXCEPTION);  // 자기가 만든 quiz 아님
        }
    }

    public void managerUploadImage(Long quizId, String base64String) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);
        if (quiz.isPresent()) {
            if (quiz.get().getHasImage().equals(false)) {
                saveImage(quizId, base64String);

                quiz.get().setHasImage(true);
                quizRepository.save(quiz.get());

                log.info("Quiz image upload [quizId: {}]", quizId);
            } else {
                // log.debug("이미 이미지가 존재하는 quiz ID");
                throw new ApiException(ApiErrorType.DUPLICATE_IMAGE);
            }
        } else {
            // log.debug("존재하지 않는 quiz ID");
            throw new ApiException(ApiErrorType.NOT_FOUND_ID);
        }
    }
}
