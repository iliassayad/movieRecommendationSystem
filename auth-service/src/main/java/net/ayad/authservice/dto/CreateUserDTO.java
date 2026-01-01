package net.ayad.authservice.dto;

public record CreateUserDTO(
        String username,
        String password,
        String role
) {
}
