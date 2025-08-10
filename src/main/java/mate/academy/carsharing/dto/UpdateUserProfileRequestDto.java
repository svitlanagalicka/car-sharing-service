package mate.academy.carsharing.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserProfileRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName) {
}
