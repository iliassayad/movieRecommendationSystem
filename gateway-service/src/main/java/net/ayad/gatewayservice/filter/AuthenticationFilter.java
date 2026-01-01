package net.ayad.gatewayservice.filter;

import net.ayad.gatewayservice.exception.InvalidTokenException;
import net.ayad.gatewayservice.exception.MissingAuthorizationHeaderException;
import net.ayad.gatewayservice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {

        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            if (validator.isSecured.test(exchange.getRequest())){
                if (!exchange.getRequest().getHeaders().containsKey("Authorization")) {
                    throw new MissingAuthorizationHeaderException();
                }

                String authHeader = exchange.getRequest().getHeaders().getOrEmpty("Authorization").get(0);
                if(authHeader != null && authHeader.startsWith("Bearer ")){
                    authHeader = authHeader.substring(7);
                }
                try {
                    // Rest call to auth-service to validate the token
//                    restTemplate.getForObject("http://auth-service/auth/validate?token=" + authHeader, String.class);
                    jwtUtil.validateToken(authHeader);

                    ServerHttpRequest request = exchange.getRequest()
                            .mutate()
                            .header("userId", jwtUtil.getUserIdFromToken(authHeader).toString())
                            .header("role", jwtUtil.extractUserRole(authHeader))
                            .build();
                    exchange = exchange.mutate().request(request).build();

                } catch (Exception e) {
                    throw new InvalidTokenException();
                }
            }

            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Put the configuration properties
    }
}
