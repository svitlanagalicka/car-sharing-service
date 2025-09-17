package mate.academy.carsharing.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RentalRequestDto(
        @NotNull
        Long carId,
        @NotNull
        LocalDate returnDate) {
}
