package mate.academy.carsharing.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.UpdateUserProfileRequestDto;
import mate.academy.carsharing.dto.UpdateUserRoleRequestDto;
import mate.academy.carsharing.dto.UserResponseDto;
import mate.academy.carsharing.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    public UserResponseDto updateUserRole(@PathVariable Long id,
                                          @RequestBody @Valid UpdateUserRoleRequestDto
                                                  roleRequestDto) {
        return userService.updateUserRole(id, roleRequestDto);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public UserResponseDto getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public UserResponseDto updateUserProfile(
            Authentication authentication,
            @RequestBody @Valid UpdateUserProfileRequestDto profileRequestDto) {
        String email = authentication.getName();
        return userService.updateUserProfile(email, profileRequestDto);
    }
}
