package com.example.csemaster.entity;

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
@Table(name = "detail_subject")
public class DetailSubjectEntity {
    @Id
    @Column(name = "subject_id_for_detail_subject")
    private Long subjectId;

    @OneToOne
    @JoinColumn(name = "subject_id_for_detail_subject", referencedColumnName = "subject_id")
    private SubjectEntity subjectEntity;

    @Id
    @Column(name = "detail_subject")
    private String detailSubject;
}
