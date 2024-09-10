package com.example.csemaster.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<ErrorResponse> exceptionHandler(HttpServletRequest request, final ApiException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ErrorResponse.builder()
                        .errorType(e.getError().getError())
                        .errorDescription(e.getDescription())
                        .build());
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponse> exceptionHandler(HttpServletRequest request, final RuntimeException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ApiErrorType.RUNTIME_EXCEPTION.getStatus())
                .body(ErrorResponse.builder()
                        .errorType(ApiErrorType.RUNTIME_EXCEPTION.getError())
                        .errorDescription(e.getMessage())
                        .build());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> exceptionHandler(HttpServletRequest request, final MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(ApiErrorType.ARGS_NOT_VALID.getStatus())
                .body(ErrorResponse.builder()
                        .errorType(ApiErrorType.ARGS_NOT_VALID.getError())
                        .errorDescription(e.getMessage())
                        .build());
    }
}
