package mate.academy.carsharing.service;

import mate.academy.carsharing.dto.UpdateUserProfileRequestDto;
import mate.academy.carsharing.dto.UpdateUserRoleRequestDto;
import mate.academy.carsharing.dto.UserRequestDto;
import mate.academy.carsharing.dto.UserResponseDto;
import mate.academy.carsharing.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRequestDto userRequestDto) throws RegistrationException;

    UserResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto roleRequestDto);

    UserResponseDto getUserByEmail(String email);

    UserResponseDto updateUserProfile(String email, UpdateUserProfileRequestDto profileRequestDto);
}
