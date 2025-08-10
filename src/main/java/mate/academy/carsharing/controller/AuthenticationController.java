package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.UserLoginRequestDto;
import mate.academy.carsharing.dto.UserLoginResponseDto;
import mate.academy.carsharing.dto.UserRequestDto;
import mate.academy.carsharing.dto.UserResponseDto;
import mate.academy.carsharing.exception.RegistrationException;
import mate.academy.carsharing.security.AuthenticationService;
import mate.academy.carsharing.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    @Operation(summary = "user login",
            description = "user login by email and password")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/registration")
    public UserResponseDto registerUser(@RequestBody @Valid UserRequestDto userRequestDto)
            throws RegistrationException {
        return userService.register(userRequestDto);
    }
}
