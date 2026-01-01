package net.ayad.authservice.service;

import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import net.ayad.authservice.dto.AuthRequest;
import net.ayad.authservice.dto.AuthResponse;
import net.ayad.authservice.entity.User;
import net.ayad.authservice.repository.UserRepository;
import net.ayad.authservice.utility.JWTUtility;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTUtility jwtUtility;

    public AuthResponse authenticate(AuthRequest authRequest) {
        Authentication authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.username(),
                        authRequest.password()
                )
        );

        User user = userRepository.findByUsername(authRequest.username())
                .orElseThrow(() -> new RuntimeException("User not found"));


        return new AuthResponse(jwtUtility.generateToken(authRequest.username(), user.getUserId(), user.getRole()));
    }

    public Boolean validateToken(String token) {
        return jwtUtility.validateToken(token);
    }


}
