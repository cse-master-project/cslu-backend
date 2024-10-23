package com.example.csemaster.v2.domain.quiz.report;

import com.example.csemaster.core.dao.actor.ActiveUserEntity;
import com.example.csemaster.core.dao.quiz.accessory.QuizReportEntity;
import com.example.csemaster.core.exception.ApiErrorType;
import com.example.csemaster.core.exception.ApiException;
import com.example.csemaster.core.repository.ActiveUserRepository;
import com.example.csemaster.core.repository.QuizReportRepository;
import com.example.csemaster.v2.dto.response.QuizReportResponse;
import com.example.csemaster.v2.mapper.QuizReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service(value = "V2QuizReportService")
@RequiredArgsConstructor
public class ReportService {
    private final QuizReportRepository quizReportRepository;
    private final ActiveUserRepository activeUserRepository;
    private final QuizReportMapper quizReportMapper;

    // 모든 신고 조회 (페이징)
    public Page<QuizReportResponse> allQuizReport(Pageable pageable) {
        Page<QuizReportEntity> quizReports = quizReportRepository.findAll(pageable);

        return quizReports
                .map(quizReport -> {
                    QuizReportResponse response = QuizReportMapper.INSTANCE.toQuizReportResponse(quizReport);
                    ActiveUserEntity activeUser = activeUserRepository.findById(quizReport.getUserId()).orElse(null);
                    if (activeUser != null) response.setUserNickname(activeUser.getNickname());
                    else response.setUserNickname("탈퇴한 사용자");

                    return response;
                });
    }

    public Page<QuizReportResponse> getUnProcessedQuizReport(Pageable pageable) {
        Page<QuizReportEntity> quizReports = quizReportRepository.findUnprocessed(pageable);

        return quizReports.map(quizReport -> {
            QuizReportResponse response = QuizReportMapper.INSTANCE.toQuizReportResponse(quizReport);
            ActiveUserEntity activeUser = activeUserRepository.findById(quizReport.getUserId()).orElse(null);
            if (activeUser != null) response.setUserNickname(activeUser.getNickname());
            else response.setUserNickname("탈퇴한 사용자");

            return response;
        });
    }

    // 신고 조회 (신고 아이디로 조회)
    public QuizReportResponse getQuizReport(Long quizReportId) {
        Optional<QuizReportEntity> quizReport = quizReportRepository.findByQuizReportId(quizReportId);

        // quizReportId가 존재하는지 확인
        if (quizReport.isEmpty()) {
            throw new ApiException(ApiErrorType.NOT_FOUND_ID);
        }
        QuizReportResponse response = quizReportMapper.toQuizReportResponse(quizReport.get());
        ActiveUserEntity activeUser = activeUserRepository.findById(quizReport.get().getUserId()).orElse(null);
        if (activeUser != null) response.setUserNickname(activeUser.getNickname());
        else response.setUserNickname("탈퇴한 사용자");

        return response;
    }

    // 특정 문제의 모든 신고 조회 (퀴즈 아이디로 조회) (페이징)
    public Page<QuizReportResponse> getAllReportForQuiz(Long quizId, Pageable pageable) {
        Page<QuizReportEntity> quizReports = quizReportRepository.findByQuizId(quizId, pageable);

        return quizReports
                .map(quizReport -> {
                    QuizReportResponse response = QuizReportMapper.INSTANCE.toQuizReportResponse(quizReport);
                    ActiveUserEntity activeUser = activeUserRepository.findById(quizReport.getUserId()).orElse(null);
                    if (activeUser != null) response.setUserNickname(activeUser.getNickname());
                    else response.setUserNickname("탈퇴한 사용자");

                    return response;
                });
    }
}
