package mate.academy.carsharing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.model.Car;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CarRequestDto {
    @NotBlank
    private String model;
    @NotBlank
    private String brand;
    @NotNull
    private Car.CarType type;
    @PositiveOrZero
    private int inventory;
    @NotNull
    private BigDecimal dailyFee;
}
