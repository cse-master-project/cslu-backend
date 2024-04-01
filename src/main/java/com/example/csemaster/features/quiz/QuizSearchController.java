package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.request.QuizSolverRequest;
import com.example.csemaster.dto.response.QuizRejectResponse;
import com.example.csemaster.dto.response.QuizResponse;
import com.example.csemaster.entity.ActiveQuizEntity;
import com.example.csemaster.dto.response.UserQuizResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
public class QuizSearchController {
    private final QuizSearchService quizSearchService;
    private final QuizSolverService quizSolverService;
    private final NewQuizService newQuizService;

    @GetMapping("/")
    public Page<ActiveQuizEntity> getAllQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getQuiz(pageRequest);
    }

    @GetMapping("/user")
    public Page<ActiveQuizEntity> getUserQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getUserQuiz(pageRequest);
    }

    @GetMapping("/default")
    public Page<ActiveQuizEntity> getDefaultQuiz(@ModelAttribute PageRequest pageRequest) {
        return quizSearchService.getDefaultQuiz(pageRequest);
    }

    @GetMapping("/random")
    public QuizResponse getRandomQuiz(@RequestParam String subject, @RequestParam String detailSubject) {
        // 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 무작위로 하나의 문제를 반환
        return quizSolverService.getQuiz(userId, subject, detailSubject);
    }

    @PostMapping("/solver")
    public ResponseEntity<?> solveQuiz(@RequestBody QuizSolverRequest request)  {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        return quizSolverService.saveQuizResult(userId, request.getQuizId(), request.getIsCorrect());
    }

    @GetMapping("/userQuiz")
    public List<UserQuizResponse> getUserQuiz(@RequestParam String userId) {
        return quizSearchService.getUserQuiz(userId);
    }

    @GetMapping("/reject")
    public List<QuizRejectResponse> getQuizReject(@RequestParam Long quizId) {
        return quizSearchService.getQuizReject(quizId);
    }
}
