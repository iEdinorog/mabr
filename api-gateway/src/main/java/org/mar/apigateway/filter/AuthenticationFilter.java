package org.mar.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
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
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization header");
                }

                var authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    var token = authHeader.substring(7);

                    return builder.build().get()
                            .uri("http://authentication-service/auth/validate")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .retrieve()
                            .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                                    Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token")))
                            .toBodilessEntity()
                            .flatMap(response -> {
                                if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {

                                    exchange.getRequest()
                                            .mutate()
                                            .header("authorUser", response.getHeaders().getFirst("authorUser"));

                                    return chain.filter(exchange);

                                } else {
                                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
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
