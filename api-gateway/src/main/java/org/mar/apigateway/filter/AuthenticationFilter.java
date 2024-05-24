package org.mar.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final WebClient.Builder builder;

    public AuthenticationFilter(RouteValidator validator, WebClient.Builder builder) {
        super(Config.class);
        this.validator = validator;
        this.builder = builder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                var authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    var token = authHeader.substring(7);

                    return builder.build().get()
                            .uri("http://authentication-service/auth/validate")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .retrieve()
                            .toBodilessEntity()
                            .flatMap(response -> {
                                if (response.getStatusCode().is2xxSuccessful()) {
                                    return chain.filter(exchange);
                                } else {
                                    return Mono.error(new RuntimeException("Invalid token"));
                                }
                            });
                }
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
