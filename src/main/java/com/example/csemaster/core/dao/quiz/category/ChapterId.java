package com.example.csemaster.core.dao.quiz.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Data
public class ChapterId implements Serializable {
    private Long subjectId;
    private String chapter;
}
