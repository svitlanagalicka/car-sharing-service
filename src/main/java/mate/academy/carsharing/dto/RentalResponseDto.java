package mate.academy.carsharing.dto;

import java.time.LocalDate;

public record RentalResponseDto(
        Long id,
        LocalDate rentalDate,
        LocalDate returnDate,
        LocalDate actualReturnDate,
        Long carId,
        Long userId) {
}
