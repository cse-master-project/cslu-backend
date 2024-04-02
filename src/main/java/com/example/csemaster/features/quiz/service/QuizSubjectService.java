package com.example.csemaster.features.quiz.service;

import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.entity.SubjectEntity;
import com.example.csemaster.entity.DetailSubjectEntity;
import com.example.csemaster.repository.QuizSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuizSubjectService {

    private final QuizSubjectRepository quizSubjectRepository;

    public List<SubjectResponse> getAllSubject() {
        List<SubjectEntity> subjects = quizSubjectRepository.findAll();
        return subjects.stream().map(subject -> {
            SubjectResponse response = new SubjectResponse();
            response.setSubjectId(subject.getSubjectId());
            response.setSubject(subject.getSubject());
            response.setDetailSubject(subject.getDetailSubjects().stream()
                    .map(DetailSubjectEntity::getDetailSubject)
                    .collect(Collectors.toList()));
            return response;
        }).collect(Collectors.toList());
    }
}
