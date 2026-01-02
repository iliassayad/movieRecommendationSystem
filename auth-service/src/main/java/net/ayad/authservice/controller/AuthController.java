package net.ayad.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.ayad.authservice.dto.AuthRequest;
import net.ayad.authservice.dto.AuthResponse;
import net.ayad.authservice.dto.CreateUserDTO;
import net.ayad.authservice.dto.UserResponseDTO;
import net.ayad.authservice.service.AuthService;
import net.ayad.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication Controller", description = "APIs for user authentication and registration")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(summary = "User Registration", description = "Register a new user with the provided details")
    @ApiResponse(responseCode = "200", description = "Successfully registered the user")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserDTO createUserDTO) {
        UserResponseDTO userResponseDTO = userService.createUser(createUserDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "User Login", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Successfully authenticated the user")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authService.authenticate(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        if (!authService.validateToken(token)) {
            return "Invalid Token";
        }
        return "Token is valid";
    }


}
