package com.example.csemaster.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwt = "JWT";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        );

        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(securityRequirement)
                .components(components);
    }

    @Bean
    public GroupedOpenApi groupV2() {
        return GroupedOpenApi.builder()
                .group("V2")
                .pathsToMatch("/api/v2/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupQuiz() {
        return GroupedOpenApi.builder()
                .group("Quiz")
                .pathsToMatch("/api/v2/quiz/**")
                .build();
    }

    @Bean
    public GroupedOpenApi groupAccount() {
        return GroupedOpenApi.builder()
                .group("Account")
                .pathsToMatch("/api/v2/user/**", "/api/v2/manager/**")
                .build();
    }

    private Info apiInfo() {
        return new Info()
                .title("CSLU API") // API의 제목
                .description("""
                        ## CSLU API Documentation
                        
                        문제 풀이 앱 CSLU의 백엔드를 지원하는 API입니다.
                        
                        #### 역할별 주요기능
                        1. 일반 사용자
                        - 문제 풀이 : 랜덤 문제 조회, 풀이 결과 전송
                        - 문제 작성 : 문제 생성 및 조회
                        - 정보 관리 : 프로필 관리, 풀이 통계 조회
                        2. 관리자
                        - 문제 관리 : 문제 생성 및 수정, 삭제
                        - 문제 카테고리 관리 : 과목&챕터 생성 및 수정, 삭제
                        
                        #### 인증
                        - JWT 사용
                        
                        [*디버깅용 로그 페이지*](https://cslu.kro.kr:8080/dev/log)
                        """) // API에 대한 설명
                .version("2.2.0"); // API의 버전
    }
}
