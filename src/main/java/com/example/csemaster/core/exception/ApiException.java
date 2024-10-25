package com.example.csemaster.core.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ApiErrorType error;
    private final String description;

    public ApiException(ApiErrorType e) {
        this.error = e;
        this.description = e.getDescription();
    }
    public ApiException(ApiErrorType e, String message) {
        this.error = e;
        this.description = message;
    }
}
