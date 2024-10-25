package com.example.csemaster.core.tools;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Constraint(validatedBy = PageableValidator.class)
// 이 어노테이션이 유효성 검사 제약 조건임을 나타냄
// validatedBy 속성은 이 제약 조건을 검증하는 데 사용할 클래스를 지정함
@Target({ElementType.FIELD, ElementType.PARAMETER})
// 자바에서 해당 어노테이션이 적용될 수 있는 대상 정의
// FIELD : 클래스의 필드(변수)에 적용될 수 있음, ANNOTATION_TYPE : 다른 어노테이션 정의에 적용될 수 있음
@Retention(RetentionPolicy.RUNTIME) // 해당 어노테이션이 언제까지 유지될지 정의
public @interface ValidPageable {
    String message() default "Invalid pageable parameters"; // 기본 메시지 추가

    Class<?>[] value() default {};
    Class<?>[] groups() default {};
    Class<?>[] payload() default {};
}
