package com.rnsd.mystorage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myStorageOpenAPI() {
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
                );
    }

}
