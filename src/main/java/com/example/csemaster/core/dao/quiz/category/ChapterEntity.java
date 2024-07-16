package com.example.csemaster.core.dao.quiz.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(ChapterId.class)
@Table(name = "detail_subject")
public class ChapterEntity {
    @Id
    @Column(name = "subject_id_for_detail_subject")
    private Long subjectId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject_id_for_detail_subject", referencedColumnName = "subject_id", insertable = false, updatable = false)
    private SubjectEntity subjectEntity;

    @Column(name = "detail_subject")
    private String chapter;

    @Column(name = "sort_index")
    private Integer sortIndex;

    public ChapterEntity(Long id, String chapter, Integer idx) {
        this.subjectId = id;
        this.chapter = chapter;
        this.sortIndex = idx;
    }
}
