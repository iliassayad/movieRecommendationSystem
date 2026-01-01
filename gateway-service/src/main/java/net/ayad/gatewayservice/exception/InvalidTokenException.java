package net.ayad.gatewayservice.exception;

public class InvalidTokenException extends JwtAuthenticationException{
    public InvalidTokenException() {
        super("The provided token is invalid.", "INVALID_TOKEN");
    }
}
