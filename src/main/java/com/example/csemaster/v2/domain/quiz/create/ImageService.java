package com.example.csemaster.v2.domain.quiz.create;

import com.example.csemaster.core.dao.quiz.core.QuizEntity;
import com.example.csemaster.core.dao.quiz.core.UserQuizEntity;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.repository.QuizRepository;
import com.example.csemaster.core.repository.UserQuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service(value = "V2ImageService")
@Slf4j
@RequiredArgsConstructor
public class ImageService {
    private final UserQuizRepository userQuizRepository;
    private final QuizRepository quizRepository;

    @Value("${img.file.path}")
    private String imgPath;

    // FIXME : 현재 파일 확장자는 오류로 인해 jpg 만 가능하게 했지만, 나중에 png, jpeg 도 되게 할 것
    private String getFileExtension(String[] strings, Long quizId) {
        System.out.println(strings[0].toLowerCase());
        if (strings[0].toLowerCase().contains("png")) {
            return quizId.toString() + ".png";
        } else if (strings[0].toLowerCase().contains("jpeg") || strings[0].toLowerCase().contains("jpg")) {
            return quizId.toString() + ".jpg";
        } else {
            throw new ApiException(ApiErrorType.UNSUPPORTED_FILE_EXTENSION);
        }
    }

    // TODO : core 패키지로 옮기기
    // 이미지 파일 임시 저장
    public String saveImage(String base64String) {
        try {
            String[] strings = base64String.split(",");
            String uuid = UUID.randomUUID().toString();
            String filename = imgPath + "/temp/" + uuid + ".jpg";  // 무조건 jpg 로 저장

            File directory = new File(imgPath + "/temp");
            if (!directory.exists()) {
                directory.mkdirs(); // 폴더가 존재하지 않는다면 생성
            }

            byte[] decodedBytes = Base64.getDecoder().decode(strings[1]);
            System.out.println(filename);
            FileOutputStream fos = new FileOutputStream(filename, false);
            fos.write(decodedBytes);
            fos.close();

            return uuid;
            // log.info("file save successful. [quizId: " + quizId + "]");
        } catch (IOException e) {
            // 입출력 실패시 500
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR, "image save error");
        }
    }

    // 이미지 복사 (이미지 최종 저장)
    private void copyImage(Long quizId, String uuid) {
        try {
            Path tempPath = Paths.get(imgPath + "/temp", uuid + ".jpg");
            Path finalPath = Paths.get(imgPath, quizId + ".jpg");

            Files.copy(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR, "image copy error");
        }
    }

    // 유저 이미지 업로드
    @Transactional
    public void userUploadImage(String userId, Long quizId, String uuid) {
        Optional<UserQuizEntity> userQuiz = userQuizRepository.findByQuizIdAndUserId(quizId, userId);
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);
        if (userQuiz.isPresent() && quiz.isPresent()) {
            if (quiz.get().getHasImage().equals(false)) {
                copyImage(quizId, uuid);

                userQuiz.get().getQuiz().setHasImage(true);
                quizRepository.save(userQuiz.get().getQuiz());
                log.info("Quiz image upload [quizId: {}, userId: {}]", quizId, userId);
            } else {
                // log.debug("이미 이미지가 존재하는 quiz ID");
                throw new ApiException(ApiErrorType.DUPLICATE_IMAGE);
            }
        } else {
            if (quiz.isEmpty()) throw new ApiException(ApiErrorType.NOT_FOUND_ID);  // 존재하지 않는 quiz id
            else throw new ApiException(ApiErrorType.ACCESS_DENIED_EXCEPTION);  // 자기가 만든 quiz 아님
        }
    }

    // 매니저 이미지 업로드
    public void managerUploadImage(Long quizId, String uuid) {
        Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);
        if (quiz.isPresent()) {
            if (quiz.get().getHasImage().equals(false)) {
                copyImage(quizId, uuid);

                quiz.get().setHasImage(true);
                quizRepository.save(quiz.get());

                log.info("Quiz image upload [quizId: {}]", quizId);
            } else {
                // log.debug("이미 이미지가 존재하는 quiz ID");
                throw new ApiException(ApiErrorType.DUPLICATE_IMAGE);
            }
        } else {
            // log.debug("존재하지 않는 quiz ID");
            throw new ApiException(ApiErrorType.NOT_FOUND_ID);
        }
    }

    // 문제 이미지 조회
    public ResponseEntity<?> getQuizImage(Long quizId) {
        try {
            BufferedImage image = ImageIO.read(new File(imgPath + "/" + quizId +".jpg"));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            ImageIO.write(image, "jpg", baos);
            byte[] imageData = baos.toByteArray();

            String base64Image = Base64.getEncoder().encodeToString(imageData);

            return ResponseEntity.ok().body(base64Image);
        } catch (IOException e) {
            log.error(e.toString());
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR);
        }
    }

    // 이미지 삭제
    public ResponseEntity<?> deleteImage(Long quizId) {
        try {
            Optional<QuizEntity> quiz = quizRepository.findByQuizId(quizId);

            if (quiz.isPresent()) {
                if (quiz.get().getHasImage().equals(true)) {
                    Path path = Paths.get(imgPath, quizId + ".jpg");
                    Files.delete(path);

                    quiz.get().setHasImage(false);
                    quizRepository.save(quiz.get());

                    log.info("Quiz image deleted [quizId: {}]", quizId);
                    return ResponseEntity.ok().build();
                } else {
                    throw new ApiException(ApiErrorType.NOT_FOUND_IMAGE);
                }
            } else {
                throw new ApiException(ApiErrorType.NOT_FOUND_ID);
            }
        } catch (IOException e) {
            throw new ApiException(ApiErrorType.INTERNAL_SERVER_ERROR, "image delete error");
        }
    }
}
