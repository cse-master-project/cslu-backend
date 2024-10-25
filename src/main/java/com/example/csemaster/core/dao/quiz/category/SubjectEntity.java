package com.example.csemaster.core.dao.quiz.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "subject")
public class SubjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "subject")
    private String subject;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "subject_id_for_detail_subject", insertable = false, updatable = false)
    @OrderBy("sortIndex ASC")
    private List<ChapterEntity> chapters;
}
