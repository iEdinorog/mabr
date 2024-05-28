package org.mar.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class RouteValidator {

    public static final List<Pattern> openApiEndpoints = List.of(
            Pattern.compile("^/auth$"),
            Pattern.compile("^/auth/validate$"),
            Pattern.compile("^/user/create$"),
            Pattern.compile("^/user/[^/]+$")
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());
}
