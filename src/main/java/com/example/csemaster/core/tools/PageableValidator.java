package com.example.csemaster.core.tools;

import jakarta.persistence.Entity;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableValidator implements ConstraintValidator<ValidPageable, Pageable> {
    private Class<?>[] entityClasses;

    public void initialize(ValidPageable constraintAnnotation) {
        this.entityClasses = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Pageable pageable, ConstraintValidatorContext constraintValidatorContext) {
        // Pageable 객체에서 Sort 값만 추출
        Sort sort = pageable.getSort();
        // 정렬 기준 (리스트) 순회
        for (Sort.Order order : sort.toList()) {
            // 설정한 프로퍼티(필드) 값 가져오기
            String field = order.getProperty();

            // 입력받은 프로퍼티 값이 검색할 엔티티에 있는 필드 값인지 확인
            for (Class<?> entityClass : entityClasses) {
                if (isInvalidClass(entityClass, field)) {
                    constraintValidatorContext.disableDefaultConstraintViolation();
                    constraintValidatorContext.buildConstraintViolationWithTemplate("Invalid field name")
                            .addConstraintViolation();
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isInvalidClass(Class<?> entityClass, String field) {
        try {
            Class<?> clazz = entityClass.getDeclaredField(field).getType();
            // 해당 어노테이션에 입력한 클래스가 엔티티클래스가 아닌지 확인 (엔티티클래스면 false)
            return clazz.getAnnotation(Entity.class) != null;
        } catch (Exception e) {
            return true;
        }
    }
}
