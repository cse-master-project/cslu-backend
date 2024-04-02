package com.example.csemaster.repository;

import com.example.csemaster.entity.UserQuizEntity;
import com.example.csemaster.dto.UnApprovalQuizDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserQuizRepository extends JpaRepository<UserQuizEntity, Long> {
    @Query(" SELECT new com.example.csemaster.dto.UnApprovalQuizDTO(q.quiz, u.nickname) " +
            "FROM UserQuizEntity q " +
            "JOIN ActiveUserEntity u ON u.userId = q.userId.userId " +
            "WHERE q.permissionStatus = 0")
    List<UnApprovalQuizDTO> getAnApprovalQuiz();

    @Query("SELECT q FROM UserQuizEntity q " +
            "WHERE q.userQuizId = :quizId AND q.userId.userId = :userId")
    Optional<UserQuizEntity> findByQuizIdAndUserId(Long quizId, String userId);
}
