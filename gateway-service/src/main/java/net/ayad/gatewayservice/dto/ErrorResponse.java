package net.ayad.gatewayservice.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        String errorCode,
        String message,
        String path,
        LocalDateTime timestamp
) {

}
