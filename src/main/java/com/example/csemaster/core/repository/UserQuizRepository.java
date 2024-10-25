package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.core.UserQuizEntity;
import com.example.csemaster.v2.dto.UnApprovalQuizDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserQuizRepository extends JpaRepository<UserQuizEntity, Long> {
    @Query("SELECT new com.example.csemaster.v1.dto.UnApprovalQuizDTO(q.quiz, u.nickname) " +
            "FROM UserQuizEntity q " +
            "JOIN ActiveUserEntity u ON u.userId = q.userId.userId " +
            "WHERE q.permissionStatus = 0")
    List<com.example.csemaster.v1.dto.UnApprovalQuizDTO> getAnApprovalQuizV1();
    @Query("SELECT new com.example.csemaster.v2.dto.UnApprovalQuizDTO(q.quiz, u.nickname) " +
            "FROM UserQuizEntity q " +
            "JOIN ActiveUserEntity u ON u.userId = q.userId.userId " +
            "WHERE q.permissionStatus = 0")
    Page<UnApprovalQuizDTO> getAnApprovalQuiz(Pageable pageable);

    @Query("SELECT q FROM UserQuizEntity q " +
            "WHERE q.userQuizId = :quizId AND q.userId.userId = :userId")
    Optional<UserQuizEntity> findByQuizIdAndUserId(Long quizId, String userId);
}
