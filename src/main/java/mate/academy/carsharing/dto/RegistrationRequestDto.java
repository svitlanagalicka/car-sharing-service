package mate.academy.carsharing.dto;

public record RegistrationRequestDto(
        String email,
        String password,
        String repeatPassword,
        String firstName,
        String lastName,
        String role) {
}
