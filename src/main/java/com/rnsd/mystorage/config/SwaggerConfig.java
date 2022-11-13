package com.rnsd.mystorage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myStorageOpenAPI() {
        String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Складской учет")
                                .version("1.0.0")
                                .description("Приложение по учету товаров на складе")
                                .contact(
                                        new Contact()
                                                .name("Рогова Наталия Викторовна")
                                                .email("rogova.nataliya1999@gmail.com")
                                )
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(
                        securitySchemeName,
                        new SecurityScheme().name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ));
    }

}
