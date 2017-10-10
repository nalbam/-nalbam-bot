package com.nalbam.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${nalbam.application.base}")
    private String base;

    @Value("${nalbam.application.name}")
    private String name;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(this.base))
                .paths(PathSelectors.any())
                .build()
                //.ignoredParameterTypes(Pageable.class)
                .apiInfo(new ApiInfo(this.name, "", "", "", ApiInfo.DEFAULT_CONTACT, "", "", new ArrayList<>()));
    }

}
