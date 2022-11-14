package com.rnsd.mystorage.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Класс для конфигурации Swagger, с настройками того, что будет показываться на UI
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myStorageOpenAPI() {
        String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .servers(List.of(new Server().url("http://localhost:8080"), new Server().url("http://pet.rnsd.fun")))
                .info(
                        new Info()
                                .title("Складской учет")
                                .version("1.0.0")
                                .description("Приложение по учету товаров на складе. \n Для использования приложения " +
                                        "необходимо зарегестрироваться по /sign-up (доступна только роль USER). После " +
                                        "регистрации пользователь должен аутентифицироваться по /login и провести " +
                                        "авторизацию (Authorize) путем применения полученного на /login access токена. " +
                                        "После применения токена для пользователя с ролью USER будут доступны все " +
                                        "endpoint'ы, кроме /create-user, который доступен только пользователю с ролью " +
                                        "ADMIN. /create-user позволяет создавать других пользователей с ролью ADMIN. " +
                                        "\n После истечения срока действия access токена, доступ снова можно получить " +
                                        "по /token с помощью еще действующего refresh токена. В случае если оба токена " +
                                        "истекли необходимо снова обратится на /login.")
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
