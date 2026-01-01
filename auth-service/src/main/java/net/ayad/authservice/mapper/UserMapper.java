package net.ayad.authservice.mapper;

import net.ayad.authservice.dto.CreateUserDTO;
import net.ayad.authservice.dto.UserResponseDTO;
import net.ayad.authservice.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserDTO createUserDTO);

    UserResponseDTO toUserResponseDTO(User savedUser);
}
