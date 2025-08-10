package mate.academy.carsharing.service;

import jakarta.transaction.Transactional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.UpdateUserProfileRequestDto;
import mate.academy.carsharing.dto.UpdateUserRoleRequestDto;
import mate.academy.carsharing.dto.UserRequestDto;
import mate.academy.carsharing.dto.UserResponseDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.exception.RegistrationException;
import mate.academy.carsharing.mapper.UserMapper;
import mate.academy.carsharing.model.Role;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.RoleRepository;
import mate.academy.carsharing.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Transactional
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRequestDto userRequestDto) throws RegistrationException {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RegistrationException("Such email already exists");
        }
        User user = userMapper.requestDtoToUser(userRequestDto);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        Role role = roleRepository.findByRole(Role.RoleName.CUSTOMER);
        user.setRoles(Set.of(role));
        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserResponseDto updateUserRole(Long id, UpdateUserRoleRequestDto roleRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        Role role = roleRepository.findByRole(roleRequestDto.role());
        if (role == null) {
            throw new EntityNotFoundException("Role not found: " + roleRequestDto.role());
        }
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
        return toUserResponseDto(user);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new EntityNotFoundException("User not found with email: " + email));

        return toUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUserProfile(String email,
                                             UpdateUserProfileRequestDto profileRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()
                        -> new EntityNotFoundException("User not found with email: " + email));
        user.setFirstName(profileRequestDto.firstName());
        user.setLastName(profileRequestDto.lastName());
        userRepository.save(user);
        return toUserResponseDto(user);
    }

    private UserResponseDto toUserResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setEmail(user.getEmail());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());
        return responseDto;
    }
}
