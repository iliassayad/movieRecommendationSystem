package net.ayad.authservice.dto;

public record AuthRequest(
        String username,
        String password
) {
}
