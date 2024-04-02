package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.DetailSubjectDTO;
import com.example.csemaster.dto.DetailSubjectUpdateDTO;
import com.example.csemaster.dto.SubjectUpdateDTO;
import com.example.csemaster.dto.response.SubjectDTO;
import com.example.csemaster.dto.response.SubjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz-subject")
public class QuizSubjectController {

    private final QuizSubjectService quizSubjectService;

    @GetMapping()
    public List<SubjectResponse> getAllSubject() {
        return quizSubjectService.getAllSubject();
    }

    @PostMapping("/subject")
    public ResponseEntity<?> addSubject(@RequestBody @Valid SubjectDTO subjectDTO) {
        return quizSubjectService.addSubject(subjectDTO);
    }

    @PostMapping("/detail")
    public ResponseEntity<?> addDetailSubject(@RequestBody @Valid DetailSubjectDTO detailSubjectDTO) {
        return quizSubjectService.addDetailSubject(detailSubjectDTO);
    }

    @PatchMapping("/update/subject")
    public ResponseEntity<?> updateSubject(@RequestBody @Valid SubjectUpdateDTO subjectUpdateDTO) {
        return quizSubjectService.updateSubject(subjectUpdateDTO);
    }

    @PostMapping("/update/detail")
    public ResponseEntity<?> updateDetailSubject(@RequestBody @Valid DetailSubjectUpdateDTO updateDTO) {
        return quizSubjectService.updateDetailSubject(updateDTO);
    }

    @DeleteMapping("/delete/subject")
    public ResponseEntity<?> deleteSubject(@RequestBody @Valid SubjectDTO subjectDTO) {
        return quizSubjectService.deleteSubject(subjectDTO);
    }

    @DeleteMapping("/delete/detail")
    public ResponseEntity<?> deleteDetailSubject(@RequestBody @Valid DetailSubjectDTO detailSubjectDTO) {
        return quizSubjectService.deleteDetailSubject(detailSubjectDTO);
    }

}
