package com.example.csemaster.features.quiz.controller;

import com.example.csemaster.dto.*;
import com.example.csemaster.dto.request.SubjectSortRequest;
import com.example.csemaster.dto.response.SubjectResponse;
import com.example.csemaster.features.quiz.service.QuizSubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "QuizSubject", description = "카테고리 관련 기능")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz/subject")
public class QuizSubjectController {

    private final QuizSubjectService quizSubjectService;

    // 카테고리 조회
    @Operation(
            summary = "카테고리 조회",
            description = "카테고리 및 서브 카테고리 전체를 조회"
    )
    @GetMapping()
    public List<SubjectResponse> getAllSubject() {
        return quizSubjectService.getAllSubject();
    }

    // 카테고리 추가
    @Operation(
            summary = "카테고리 추가",
            description = "새 카테고리를 받아서 추가"
    )
    @PostMapping()
    public List<String> addSubject(@RequestBody @Valid SubjectRequest subjectRequest) {
        return quizSubjectService.addSubject(subjectRequest);
    }


    // 서브 카테고리 추가
    @Operation(
            summary = "서브 카테고리 추가",
            description = "추가할 서브 카테고리의 메인 카테고리와 새 서브 카테고리를 받아서 추가"
    )
    @PostMapping("/detail")
    public List<String> addDetailSubject(@RequestBody @Valid String subject, @RequestBody @Valid String chapter) {
        return quizSubjectService.addDetailSubject(subject, chapter);
    }

    // 카테고리 수정
    @Operation(
            summary = "카테고리 수정",
            description = "카테고리와 새 카테고리를 받아서 수정"
    )
    @PatchMapping()
    public ResponseEntity<?> updateSubject(@RequestBody @Valid SubjectUpdateDTO subjectUpdateDTO) {
        return quizSubjectService.updateSubject(subjectUpdateDTO);
    }

    // 서브 카테고리 수정
    @Operation(
            summary = "서브 카테고리 수정",
            description = "수정할 서브 카테고리의 메인 카테고리와 새 서브 카테고리를 받아서 수정"
    )
    @PatchMapping("/detail")
    public ResponseEntity<?> updateDetailSubject(@RequestBody @Valid ChapterUpdateDTO updateDTO) {
        return quizSubjectService.updateDetailSubject(updateDTO);
    }

    // 카테고리 삭제
    @Operation(
            summary = "카테고리 삭제",
            description = "카테고리를 받아서 삭제"
    )
    @DeleteMapping()
    public ResponseEntity<?> deleteSubject(@RequestBody @Valid SubjectRequest subjectRequest) {
        return quizSubjectService.deleteSubject(subjectRequest);
    }

    // 서브 카테고리 삭제
    @Operation(
            summary = "서브 카테고리 삭제",
            description = "삭제할 서브 카테고리의 메인 카테고리와 서브 카테고리를 받아서 삭제"
    )
    @DeleteMapping("/detail")
    public SubjectDTO deleteDetailSubject(@RequestBody @Valid String subject, @RequestBody @Valid String chapter) {
        return quizSubjectService.deleteDetailSubject(subject, chapter);
    }

    // 서브 카테고리 순서 변경
    @Operation(
            summary = "서브 카테고리 순서 재설정",
            description = "서브 카테고리의 순서를 통째로 재설정"
    )
    @PostMapping("/detail/sort-order")
    public SubjectResponse adjustDetailSubject(@RequestBody @Valid SubjectSortRequest request) {
        return quizSubjectService.adjustDetailSubject(request.getSubject(), request.getChapters());
    }

}
