package net.ayad.gatewayservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ayad.gatewayservice.dto.ErrorResponse;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Configuration
@Order(-1) // priorité maximale
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status;
        String errorCode;

        if (ex instanceof MissingAuthorizationHeaderException) {
            status = HttpStatus.UNAUTHORIZED;
            errorCode = "AUTH_HEADER_MISSING";
        }
        else if (ex instanceof InvalidTokenException) {
            status = HttpStatus.FORBIDDEN;
            errorCode = "INVALID_JWT";
        }
        else if (ex instanceof JwtAuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            errorCode = "JWT_AUTH_ERROR";
        }
        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = "INTERNAL_ERROR";
        }

        ErrorResponse response = new ErrorResponse(
                errorCode,
                ex.getMessage(),
                exchange.getRequest().getPath().value(),
                LocalDateTime.now()
        );

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(response);
        } catch (Exception e) {
            bytes = new byte[0];
        }

        return exchange.getResponse()
                .writeWith(Mono.just(
                        exchange.getResponse()
                                .bufferFactory()
                                .wrap(bytes)
                ));
    }
}
