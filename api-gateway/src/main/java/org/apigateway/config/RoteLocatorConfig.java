package org.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoteLocatorConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        .path("/api/messenger")
                        .uri("lb://messenger-service"))
                .route(p -> p
                        .path("/api/data/**")
                        .uri("lb://post-service"))
                .route(p -> p
                        .path("/eureke/web")
                        .filters(f -> f.setPath("/"))
                        .uri("http://127.0.0.1:8761"))
                .route(p -> p
                        .path("/eureka/**")
                        .uri("http://127.0.0.1:8761"))
                .build();
    }
}
