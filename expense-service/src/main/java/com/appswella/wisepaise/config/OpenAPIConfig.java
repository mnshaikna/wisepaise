package com.appswella.wisepaise.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI wisePaisaOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("WiseCent API")
                        .description("REST API documentation for WisePaisa application")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("WisePaisa Team")
                                .email("contact@wisepaisa.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}