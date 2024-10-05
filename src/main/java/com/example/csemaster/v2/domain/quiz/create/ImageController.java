package com.example.csemaster.v2.domain.quiz.create;

import com.example.csemaster.v2.dto.request.Base64Request;
import com.example.csemaster.v2.dto.request.QuizImageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "QuizImage v2", description = "이미지 조회, 추가")
@RestController(value = "V2ImageController")
@RequiredArgsConstructor
@RequestMapping("/api/v2/quiz")
public class ImageController {
    private final ImageService imageService;

    // 이미지 추가
    @Operation(
            summary = "이미지 추가",
            description = "문제 추가 시 이미지는 해당 경로를 통해 별도로 전송해야 한다. 요청 시 필요한 문제 ID는 문제 추가 요청 시, uuid는 이미지 임시 저장 요청 시 받을 수 있다."
    )
    @PostMapping("/image")
    public void uploadImage(@RequestBody QuizImageRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();

        // 아이디의 길이로 유저, 매니저 구분
        if (id.length() <= 20) {
            imageService.managerUploadImage(request.getQuizId(), request.getUuid());
        } else {
            imageService.userUploadImage(id, request.getQuizId(), request.getUuid());
        }
    }

    // 이미지 임시 추가
    @Operation(
            summary = "이미지 임시 추가",
            description = "문제 생성 전 이미지 저장 가능 여부를 확인한다. 반환 값인 uuid(이미지 임시 저장명)가 이미지 추가 요청 시 필요하다."
    )
    @PostMapping("/image/temp")
    public String saveImage(@RequestBody Base64Request request) {
        return imageService.saveImage(request.getBase64String());
    }

    // 문제 이미지 조회
    @Operation(
            summary = "문제 이미지 조회",
            description = "문제 ID로 해당 문제의 이미지를 조회 (base64 인코딩 사용)"
    )
    @GetMapping("/{quizId}/image")
    public ResponseEntity<?> getQuizImage(@PathVariable("quizId") Long quizId) {
        // 문제가 없는 경우 예외 처리 안함
        return imageService.getQuizImage(quizId);
    }
}
