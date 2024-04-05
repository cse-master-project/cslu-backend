package com.example.csemaster.features.quiz.controller;

import com.example.csemaster.dto.DetailSubjectDTO;
import com.example.csemaster.dto.DetailSubjectUpdateDTO;
import com.example.csemaster.dto.SubjectUpdateDTO;
import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.features.quiz.service.QuizSubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz/subject")
public class QuizSubjectController {

    private final QuizSubjectService quizSubjectService;

    // 카테고리 조회
    @GetMapping()
    public List<SubjectResponse> getAllSubject() {
        return quizSubjectService.getAllSubject();
    }

    // 카테고리 추가
    @PostMapping()
    public ResponseEntity<?> addSubject(@RequestParam String subject) {
        return quizSubjectService.addSubject(subject);
    }


    // 서브 카테고리 추가
    @PostMapping("/detail")
    public ResponseEntity<?> addDetailSubject(@RequestBody @Valid DetailSubjectDTO detailSubjectDTO) {
        return quizSubjectService.addDetailSubject(detailSubjectDTO);
    }

    // 카테고리 수정
    @PatchMapping()
    public ResponseEntity<?> updateSubject(@RequestBody @Valid SubjectUpdateDTO subjectUpdateDTO) {
        return quizSubjectService.updateSubject(subjectUpdateDTO);
    }

    // 서브 카테고리 수정
    @PatchMapping("/detail")
    public ResponseEntity<?> updateDetailSubject(@RequestBody @Valid DetailSubjectUpdateDTO updateDTO) {
        return quizSubjectService.updateDetailSubject(updateDTO);
    }

    // 카테고리 삭제
    @DeleteMapping()
    public ResponseEntity<?> deleteSubject(@RequestParam Long subjectId) {
        return quizSubjectService.deleteSubject(subjectId);
    }


    // 서브 카테고리 삭제
    @DeleteMapping("/detail")
    public ResponseEntity<?> deleteDetailSubject(@RequestParam Long subjectId, String detail) {
        return quizSubjectService.deleteDetailSubject(subjectId, detail);
    }

}
