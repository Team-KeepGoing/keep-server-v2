package com.keepgoing.keepserver.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * swagger 설정 파일
 */
@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "JWT";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                                    .addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Keep")
                .description("Keep API")
                .version("0.0.2");
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name("Authorization")
                .description("Enter JWT token with **Bearer {token}**")
                .in(SecurityScheme.In.HEADER)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
