package com.example.csemaster.login.user.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserTokenResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private LocalDateTime issueAt;
    private LocalDateTime expAt;

    public static UserTokenResponse of(String accessToken, String refreshToken, LocalDateTime issueAt, LocalDateTime expAt) {
        UserTokenResponse response = new UserTokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setIssueAt(issueAt);
        response.setExpAt(expAt);

        return response;
    }
}
