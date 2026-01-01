package net.ayad.authservice.controller;

import lombok.RequiredArgsConstructor;
import net.ayad.authservice.dto.AuthRequest;
import net.ayad.authservice.dto.AuthResponse;
import net.ayad.authservice.dto.CreateUserDTO;
import net.ayad.authservice.dto.UserResponseDTO;
import net.ayad.authservice.service.AuthService;
import net.ayad.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserDTO createUserDTO) {
        UserResponseDTO userResponseDTO = userService.createUser(createUserDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

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
