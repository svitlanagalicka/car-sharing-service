package mate.academy.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Successfully registers a new user")
    void register_success() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("test@mail.com");
        userRequestDto.setPassword("1234");
        User user = new User();
        user.setEmail("test@mail.com");

        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(false);
        when(userMapper.requestDtoToUser(userRequestDto)).thenReturn(user);
        when(passwordEncoder.encode("1234")).thenReturn("encoded");
        Role role = new Role();
        role.setRole(Role.RoleName.CUSTOMER);
        when(roleRepository.findByRole(Role.RoleName.CUSTOMER)).thenReturn(role);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("test@mail.com");
        when(userMapper.userToUserDto(user)).thenReturn(userResponseDto);
        UserResponseDto result = userService.register(userRequestDto);

        assertEquals("test@mail.com", result.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Throws exception when trying to register with an existing email")
    void register_emailAlreadyExist() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail("exist@mail.com");

        when(userRepository.existsByEmail(userRequestDto.getEmail())).thenReturn(true);
        assertThrows(RegistrationException.class,
                () -> userService.register(userRequestDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Successfully updates user role")
    void updateUserRole_success() {
        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>());

        Role role = new Role();
        role.setRole(Role.RoleName.MANAGER);
        when(roleRepository.findByRole(Role.RoleName.MANAGER)).thenReturn(role);
        UpdateUserRoleRequestDto roleRequestDto = new UpdateUserRoleRequestDto(
                Role.RoleName.MANAGER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserResponseDto result = userService.updateUserRole(1L, roleRequestDto);
        assertEquals("MANAGER", result.getRole());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Throws exception when updating role of non-existing user")
    void updateUserRole_userNotFound() {
        UpdateUserRoleRequestDto roleRequestDto =
                new UpdateUserRoleRequestDto(Role.RoleName.MANAGER);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(999L, roleRequestDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Successfully gets user by email")
    void getUserByEmail_success() {
        User user = new User();
        user.setEmail("user@mail.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        Role role = new Role();
        role.setRole(Role.RoleName.CUSTOMER);
        user.setRoles(Set.of(role));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserByEmail("user@mail.com");

        assertEquals("user@mail.com", result.getEmail());
        assertEquals("CUSTOMER", result.getRole());
    }

    @Test
    @DisplayName("Throws exception when user not found by email")
    void getUserByEmail_userNotFound() {
        when(userRepository.findByEmail("nonexist@mail.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserByEmail("nonexist@mail.com"));
    }

    @Test
    @DisplayName("Successfully updates user profile")
    void updateUserProfile_success() {
        User user = new User();
        user.setEmail("user@mail.com");

        UpdateUserProfileRequestDto profileDto = mock(UpdateUserProfileRequestDto.class);
        when(profileDto.firstName()).thenReturn("New First Name");
        when(profileDto.lastName()).thenReturn("New Last Name");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));

        UserResponseDto result = userService.updateUserProfile("user@mail.com", profileDto);

        assertEquals("New First Name", result.getFirstName());
        assertEquals("New Last Name", result.getLastName());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Throws exception when updating profile of non-existing user")
    void updateUserProfile_userNotFound() {
        UpdateUserProfileRequestDto profileRequestDto =
                new UpdateUserProfileRequestDto("First name", "Last name");
        when(userRepository.findByEmail("nonexist@email.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserProfile("nonexist@email.com", profileRequestDto));
    }
}
