package com.example.csemaster.features.quiz;

import com.example.csemaster.dto.QuizUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/quiz-manager")
public class QuizManagerController {

    private final QuizManagerService quizManagerService;

    @PatchMapping("/update")
    public ResponseEntity<?> updateQuiz(@RequestBody QuizUpdateDTO quizUpdateDTO) {
        Boolean updateJsonContent = quizManagerService.updateQuiz(quizUpdateDTO);
        if(!updateJsonContent) {
            return ResponseEntity.badRequest().body("QuizUpdateController - updateQuiz()");
        }

        return ResponseEntity.ok().body("Update Successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteQuiz(@RequestParam Long quizId) {
        boolean deleteQuiz = quizManagerService.deleteQuiz(quizId);

        if (!deleteQuiz) {
            return ResponseEntity.badRequest().body("QuizDeleteController - deleteQuiz()");
        }

        return ResponseEntity.ok().body("Delete Successfully");
    }
}
