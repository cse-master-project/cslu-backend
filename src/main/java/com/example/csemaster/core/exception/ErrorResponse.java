package com.example.csemaster.core.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String errorType;
    private String errorDescription;

    @Builder
    public ErrorResponse(HttpStatus status, String errorType, String errorDescription){
        this.errorType = errorType;
        this.errorDescription = errorDescription;
    }
}
