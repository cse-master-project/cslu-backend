package com.example.csemaster.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class DetailSubjectId implements Serializable {
    private Long subjectId;
    private String detailSubject;
}
