package com.comviva.interop.txnengine.configuration;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-03-01T09:01:25.323+05:30")

@Configuration
public class SwaggerDocumentationConfig {
    // swagger configuration class interop
    ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("The APIs for the Interoperability Transaction Service Module")
                .description("Interoperability module Marroco").license("").licenseUrl("").termsOfServiceUrl("")
                .version("1.0.1").contact(new Contact("", "", "")).build();
    }

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.comviva.interop.txnengine")).build()
                .directModelSubstitute(org.joda.time.LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(org.joda.time.DateTime.class, java.util.Date.class).apiInfo(apiInfo())
                .securitySchemes(Lists.newArrayList(apiKey()));
    }

    @SuppressWarnings("deprecation")
    @Bean
    public SecurityConfiguration securityInfo() {
        return new SecurityConfiguration(null, null, null, null, "", ApiKeyVehicle.HEADER, "Authorization", "");
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    };

}