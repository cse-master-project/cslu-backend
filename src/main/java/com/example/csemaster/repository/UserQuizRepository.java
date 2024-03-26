package com.example.csemaster.repository;

import com.example.csemaster.entity.UserQuizEntity;
import com.example.csemaster.features.quiz.UnApprovalQuizDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserQuizRepository extends JpaRepository<UserQuizEntity, Long> {
    @Query(" SELECT new com.example.csemaster.features.quiz.UnApprovalQuizDTO(q.quiz, u.nickname) " +
            "FROM UserQuizEntity q " +
            "JOIN ActiveUserEntity u ON u.userId = q.userId.userId " +
            "WHERE q.permissionStatus = 0")
    List<UnApprovalQuizDTO> getAnApprovalQuiz();
}
