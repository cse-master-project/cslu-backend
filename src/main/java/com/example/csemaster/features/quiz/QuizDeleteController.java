package com.example.csemaster.features.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delete")
public class QuizDeleteController {
    private final QuizDeleteService quizDeleteService;

    @DeleteMapping()
    public ResponseEntity<?> deleteQuiz(@RequestParam Long quizId) {
        Boolean deleteQuiz = quizDeleteService.deleteQuiz(quizId);

        if (!deleteQuiz) {
            return ResponseEntity.badRequest().body("QuizDeleteController - deleteQuiz()");
        }

        return ResponseEntity.ok().body("Delete Successfully");
    }
}
