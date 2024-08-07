package org.mar.apigateway.config;

import org.mar.apigateway.filter.AuthenticationFilter;
import org.mar.apigateway.filter.AuthenticationFilter.Config;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoteLocatorConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, AuthenticationFilter authenticationFilter) {
        return builder.routes()
                .route(p -> p
                        .path("/eureka/web")
                        .filters(f -> f.setPath("/"))
                        .uri("http://127.0.0.1:8761"))
                .route(p -> p
                        .path("/eureka/**")
                        .uri("http://127.0.0.1:8761"))
                .route(p -> p
                        .path("/authentication/api/**")
                        .uri("lb://authentication-service"))
                .route(p -> p
                        .path("/notification/api/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new Config())))
                        .uri("lb://notification-service"))
                .route(p -> p
                        .path("/user/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new Config())))
                        .uri("lb://user-service"))
                .route(p -> p
                        .path("/messenger/api/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new Config())))
                        .uri("lb://messenger-service"))
                .route(p -> p
                        .path("/file-storage/api/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new Config())))
                        .uri("lb://file-storage-service"))
                .build();
    }
}
