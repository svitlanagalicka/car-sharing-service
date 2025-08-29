package mate.academy.carsharing.dto;

import java.time.LocalDate;

public record RentalRequestDto(
        Long carId,
        LocalDate returnDate) {
}
