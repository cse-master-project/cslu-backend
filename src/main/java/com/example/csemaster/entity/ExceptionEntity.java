package com.example.csemaster.entity;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionEntity {
    private String errorType;
    private String errorDescription;

    @Builder
    public ExceptionEntity(HttpStatus status, String errorType, String errorDescription){
        this.errorType = errorType;
        this.errorDescription = errorDescription;
    }
}
