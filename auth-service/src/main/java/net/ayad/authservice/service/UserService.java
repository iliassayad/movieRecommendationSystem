package net.ayad.authservice.service;

import lombok.RequiredArgsConstructor;
import net.ayad.authservice.dto.CreateUserDTO;
import net.ayad.authservice.dto.UserResponseDTO;
import net.ayad.authservice.entity.User;
import net.ayad.authservice.mapper.UserMapper;
import net.ayad.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(CreateUserDTO createUserDTO) {

        User user = userMapper.toUser(createUserDTO);
        user.setPassword(passwordEncoder.encode(createUserDTO.password()));
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponseDTO(savedUser);
    }



}
