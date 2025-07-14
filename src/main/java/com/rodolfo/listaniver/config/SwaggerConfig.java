package com.rodolfo.listaniver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pessoa CRUD API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de pessoas com operações CRUD completas")
                        .contact(new Contact()
                                .name("Equipe de Desenvolvimento")
                                .email("rodolfoguerraster@gmail.com")
                                .url("https://github.com/rodolfodecarvalho/lista-niver"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}