package com.parunev.linkededge.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * @Description: Configuration class for OpenAPI documentation.
 * @OpenAPIDefinition: This annotation defines the global configuration for OpenAPI documentation for your application.
 * @Info: Provides high-level information about your API.
 * @Server: Describes a server where the API is hosted.
 * @SecurityRequirement Specifies that the API requires the "bearerAuth" security scheme for access.
 * @SecurityScheme Defines the "bearerAuth" security scheme, which is based on JWT (JSON Web Token) authentication.
 *
 * @author Martin Parunev
 * @date October 12, 2023
 */
@OpenAPIDefinition(
        info = @Info(
        contact = @Contact(
                name = "Martin Parunev",
                email = "parunev@gmail.com"),
        description = "LinkedEdge: Unlock Your Interview Success with LinkedEdge, your AI-powered job interview preparation assistant.",
        title = "LinkedEdge",
        license = @License(
                url = "https://github.com/parunev/LinkedEdge/blob/main/LICENSE",
                name = "MIT License with Attribution Clause"
        )
),
        servers = {@Server(description = "Local Environment BE", url = "http://localhost:8080"),
        },
        security = @SecurityRequirement(name = "bearerAuth")
)

@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authentication",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
