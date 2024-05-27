package com.example.csemaster.exception;

import com.example.csemaster.entity.ExceptionEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ExceptionEntity> exceptionHandler(HttpServletRequest request, final CustomException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(e.getError().getStatus())
                .body(ExceptionEntity.builder()
                        .errorType(e.getError().getError())
                        .errorDescription(e.getError().getDescription())
                        .build());
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ExceptionEntity> exceptionHandler(HttpServletRequest request, final RuntimeException e) {
        log.error(e.getMessage());
        return ResponseEntity
                .status(ExceptionEnum.RUNTIME_EXCEPTION.getStatus())
                .body(ExceptionEntity.builder()
                        .errorType(ExceptionEnum.RUNTIME_EXCEPTION.getError())
                        .errorDescription(e.getMessage())
                        .build());
    }
}
