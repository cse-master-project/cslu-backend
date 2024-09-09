package com.example.csemaster.v2.domain.quiz.create;

import com.example.csemaster.v2.domain.quiz.create.CreateService;
import com.example.csemaster.v2.dto.QuizDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "QuizCreate v2", description = "문제 생성 기능")
@RestController(value = "V2CreateController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/quiz")
@Slf4j
public class CreateController {
    private final CreateService createService;

    // 기본 문제 추가
    @Operation(
            summary = "기본 문제 추가 [관리자 전용]",
            description = "과목, 챕터, 문제를 받아서 문제를 추가<br>관리자 아이디를 추출하여 기본 문제로 분류"
    )
    @PostMapping("/default")
    public Long addDefaultQuiz(@RequestBody @Valid QuizDTO quizDTO) {
        String managerId = SecurityContextHolder.getContext().getAuthentication().getName();

        return createService.addQuiz(quizDTO, managerId);
    }

    // 사용자 문제 추가
    @Operation(
            summary = "사용자 문제 추가 [사용자 전용]",
            description = "과목, 챕터, 문제를 받아서 문제를 추가<br>사용자 아이디를 추출하여 사용자 문제로 분류"
    )
    @PostMapping("/user")
    public Long addUserQuiz(@RequestBody @Valid QuizDTO quizDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return createService.addQuiz(quizDTO, userId);
    }
}
