package com.enigma.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Component
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.enigma.general"))
                .paths(PathSelectors.any())
                .build();
    }

//    private ApiInfo apiInfo() {
//        return new ApiInfo(
//                "Util Service",
//                "This is a Service Specification.",
//                "1.0.0",
//                "Terms of service",
//                "hsulei2610@gmail.com",
//                "License of API",
//                "API license URL");
//    }
}
