package com.example.csemaster.features.account.user;

import com.example.csemaster.dto.NicknameDTO;
import com.example.csemaster.dto.QuizResultDTO;
import com.example.csemaster.dto.response.QuizStatsResponse;
import com.example.csemaster.mapper.ActiveUserMapper;
import com.example.csemaster.repository.ActiveUserRepository;
import com.example.csemaster.repository.QuizLogRepository;
import com.example.csemaster.repository.QuizSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final ActiveUserRepository activeUserRepository;
    private final QuizLogRepository quizLogRepository;
    private final QuizSubjectRepository quizSubjectRepository;

    public ResponseEntity<?> getUserInfo(String userId) {
        return activeUserRepository.findById(userId)
                .map(activeUser -> ResponseEntity.ok().body(ActiveUserMapper.INSTANCE.toUserInfo(activeUser)))
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> setUserNickname(String userId, NicknameDTO nickNameDTO) {
        try {
            return activeUserRepository.findById(userId)
                    .map(activeUser -> {
                        activeUser.setNickname(nickNameDTO.getNickname());
                        activeUserRepository.save(activeUser);

                        return ResponseEntity.ok().build();
                    }).orElse(ResponseEntity.notFound().build());
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    // 카테고리별 정답률 구하는 함수
    private Map<String, Double> getQuizAccuracy(List<QuizResultDTO> quizResult) {
        // 모든 과목을 조회해서 카운터 객체 초기화
        QuizResultCounter quizResultCounter = new QuizResultCounter(quizSubjectRepository.getAllSubject());

        // 카운터 객체에 문제 풀이 결과 추가
        quizResult.forEach(q -> {
            try {
                quizResultCounter.pushLog(q.getSubject(), q.isCorrect());
            } catch (IllegalArgumentException e) {
                log.debug("Invalid subject.");
            }
        });

        // 정답률 계산 후 리턴
        return quizResultCounter.getCorrectRate();
    };

    // 전체 정답률 구하는 함수
    private Double getTotalAccuracy(List<QuizResultDTO> quizResult) {
        int total = 0;
        int correctCnt = 0;

        for (QuizResultDTO e : quizResult) {
            total++;
            correctCnt += e.isCorrect() ? 1 : 0;
        }

        return ((double) correctCnt / total) * 100;
    }

    public QuizStatsResponse getUserQuizResult(String userId) {
        List<QuizResultDTO> quizResult = quizLogRepository.findAllByUserId(userId);
        return new QuizStatsResponse(getTotalAccuracy(quizResult), getQuizAccuracy(quizResult));
    }
}
