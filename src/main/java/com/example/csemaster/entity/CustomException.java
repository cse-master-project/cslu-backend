package com.example.csemaster.entity;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private ExceptionEnum error;

    public CustomException(ExceptionEnum e) {
        super(e.getDescription());
        this.error = e;
    }
    public CustomException(ExceptionEnum e, String message) {
        this.error = e;
    }
}
