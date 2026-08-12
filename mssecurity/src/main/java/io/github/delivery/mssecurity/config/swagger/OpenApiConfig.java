package io.github.delivery.mssecurity.config.swagger;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Security - Delivery")
                        .version("v1")
                        .description(
                                "API responsável pelo cadastro de usuários, "
                                        + "autenticação e gerenciamento de acesso "
                                        + "do sistema de delivery."
                        ));
    }
}
