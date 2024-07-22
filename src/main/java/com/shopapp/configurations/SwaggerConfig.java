package com.shopapp.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		
		return new OpenAPI()
				.info(new Info().title("JavaInUse Authentication Service"))				
				.addSecurityItem(new SecurityRequirement().addList("JavaInUseSecurityScheme"))
				.components(new Components().addSecuritySchemes("JavaInUseSecurityScheme", new SecurityScheme()
						.name("JavaInUseSecurityScheme").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
		
	}

    // @Bean
    // public OpenAPI openAPI(){
    //     return new OpenAPI().info(new Info().title("Spring Boot Rest API")
    //                                         .description("Spring Boot Rest API")
    //                                         .contact(new Contact().name("ShopApp"))
    //                                         .version("1.0.0"));               
    // }
}
