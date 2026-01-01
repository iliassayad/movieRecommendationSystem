package net.ayad.authservice.dto;

public record UserResponseDTO(
        Long userId,
        String username,
        String role
) {
}
