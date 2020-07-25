package com.strictmanager.travelbudget.config;

import com.google.common.collect.Lists;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private final static String AUTHORIZATION = "Authorization";

    @Bean
    public Docket apiDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("api")
            .apiInfo(this.apiInfo())
            .securitySchemes(Lists.newArrayList(apiKey()))
            .securityContexts(Lists.newArrayList(securityContext()))
            .ignoredParameterTypes(AuthenticationPrincipal.class)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.strictmanager.travelbudget.web"))
            .paths(PathSelectors.ant("/api/**"))
            .build();
    }

    /**
     * dev API
     * @return
     */
    @Bean
    public Docket devDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("dev")
            .apiInfo(this.apiInfo())
            .securitySchemes(Lists.newArrayList(apiKey()))
            .securityContexts(Lists.newArrayList(securityContext()))
            .ignoredParameterTypes(AuthenticationPrincipal.class)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.strictmanager.travelbudget.web"))
            .paths(PathSelectors.ant("/dev/**"))
            .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("JWT", AUTHORIZATION, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(defaultAuth())
            .forPaths(PathSelectors.any())
            .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Travel Budget API")
            .description("Travel Budget API Doc")
            .build();
    }
}
