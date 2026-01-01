package net.ayad.gatewayservice.exception;

import lombok.Getter;

@Getter
public class MissingAuthorizationHeaderException extends JwtAuthenticationException{
    public MissingAuthorizationHeaderException() {
        super("Authorization header is missing", "AUTH_001");
    }
}
