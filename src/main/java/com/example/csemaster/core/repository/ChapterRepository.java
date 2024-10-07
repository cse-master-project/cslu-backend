package com.example.csemaster.core.repository;

import com.example.csemaster.core.dao.quiz.category.ChapterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<ChapterEntity, Long> {
    Optional<ChapterEntity> findBySubjectIdAndChapter(Long subjectId, String chapter);

    List<ChapterEntity> findBySubjectId(Long subjectId);

    @Query("SELECT d FROM ChapterEntity d " +
            "WHERE d.subjectId = (SELECT s.subjectId FROM SubjectEntity s WHERE s.subject = :subject) " +
            "ORDER BY d.sortIndex ASC ")
    List<ChapterEntity> findBySubject(String subject);

    @Query("SELECT d.chapter FROM ChapterEntity d " +
            "WHERE d.subjectId = (SELECT s.subjectId FROM SubjectEntity s WHERE s.subject = :subject) " +
            "ORDER BY d.sortIndex ASC ")
    List<String> findChapterBySubject(String subject);

    @Query("SELECT d FROM ChapterEntity d " +
            "WHERE d.subjectId = (SELECT s.subjectId FROM SubjectEntity s WHERE s.subject = :subject) " +
            "ORDER BY d.sortIndex ASC ")
    List<ChapterEntity> findChapterEntityBySubject(String subject);

    @Query("SELECT MAX(d.sortIndex) FROM ChapterEntity d " +
            "WHERE d.subjectId = (SELECT s.subjectId FROM SubjectEntity s WHERE s.subject = :subject) ")
    Optional<Integer> getMaxIndex(String subject);
}
