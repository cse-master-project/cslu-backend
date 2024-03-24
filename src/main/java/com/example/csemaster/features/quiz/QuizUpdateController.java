package com.example.csemaster.features.quiz;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/update")
public class QuizUpdateController {
    private final QuizUpdateService quizUpdateService;

    @PatchMapping()
    public ResponseEntity<?> updateQuiz(@RequestBody QuizUpdateDTO quizUpdateDTO) {
        Boolean updateJsonContent = quizUpdateService.updateQuiz(quizUpdateDTO);
        if(!updateJsonContent) {
            return ResponseEntity.badRequest().body("QuizUpdateController - updateQuiz()");
        }

        return ResponseEntity.ok().body("Update Successfully");
    }
}
