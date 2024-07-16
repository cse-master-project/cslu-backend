package com.example.csemaster.core.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ApiErrorType error;

    public ApiException(ApiErrorType e) {
        super(e.getDescription());
        this.error = e;
    }
    public ApiException(ApiErrorType e, String message) {
        this.error = e;
    }
}
