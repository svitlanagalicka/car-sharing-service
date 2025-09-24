package mate.academy.carsharing.dto;

import jakarta.validation.constraints.NotNull;
import mate.academy.carsharing.model.Role;

public record UpdateUserRoleRequestDto(@NotNull Role.RoleName role) {
}
