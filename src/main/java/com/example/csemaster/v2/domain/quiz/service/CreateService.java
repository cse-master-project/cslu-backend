package com.example.csemaster.v2.domain.quiz.service;

import com.example.csemaster.core.dao.actor.ManagerEntity;
import com.example.csemaster.core.dao.actor.UserEntity;
import com.example.csemaster.core.dao.quiz.category.ChapterEntity;
import com.example.csemaster.core.dao.quiz.category.SubjectEntity;
import com.example.csemaster.core.dao.quiz.core.DefaultQuizEntity;
import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.core.dao.quiz.core.UserQuizEntity;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.repository.*;
import com.example.csemaster.v2.dto.QuizDTO;
import com.example.csemaster.v2.mapper.AddQuizMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service(value = "V2QuizCreateService")
@Slf4j
@RequiredArgsConstructor
public class CreateService {
    private final AddQuizMapper addQuizMapper;
    private final QuizRepository quizRepository;
    private final ManagerRepository managerRepository;
    private final DefaultQuizRepository defaultQuizRepository;
    private final UserRepository userRepository;
    private final UserQuizRepository userQuizRepository;
    private final QuizSubjectRepository quizSubjectRepository;
    private final ChapterRepository chapterRepository;

    private final QuizVerificationService quizVerificationService;

    // 문제 추가
    @Transactional
    public Long addQuiz(QuizDTO quizDTO, String id) {
        // Quiz 테이블에 추가하고 ID 반환
        Long quizId = addQuizContent(quizDTO);

        if (id.length() <= 20) {
            addDefaultQuiz(quizId, id);
            log.info("Create default quiz [new quizId: {}]", quizId);
        } else {
            // 반환된 ID를 사용하여 UserQuiz 추가
            addUserQuiz(quizId, id);
            log.info("Create user quiz [new quizId: {}, userId: {}]", quizId, id);
        }

        return quizId;
    }

    // quiz 테이블에 문제 내용만 추가
    public Long addQuizContent(QuizDTO quizDTO) {
        // subject, chapter 확인
        Optional<SubjectEntity> subject = quizSubjectRepository.findBySubject(quizDTO.getSubject());
        if (subject.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_SUBJECT);
        }

        Optional<ChapterEntity> chapter = chapterRepository.findBySubjectIdAndChapter(subject.get().getSubjectId(), quizDTO.getChapter());
        if (chapter.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_CHAPTER);
        }

        // jsonContent 형식 검사
        // FIXME : 임시로 QuizVerificationService 의 변수를 사용하도록 했으니 변경 후 여기도 바꿔야함.
        if (!quizVerificationService.isValidJsonContent(quizDTO.getQuizType(), quizDTO.getJsonContent())) {
            throw new ApiException(ApiErrorType.INCORRECT_QUIZ_CONTENT);
        }

        // Quiz 테이블에 추가
        QuizEntity quizEntity = addQuizMapper.toQuizEntity(quizDTO);
        quizRepository.save(quizEntity);

        // log.info("Quiz 저장 완료");

        return quizEntity.getQuizId();
    }

    // 기본 문제 출제자 정보 추가
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

    // 사용자 문제 출제자 정보 추가
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
}
