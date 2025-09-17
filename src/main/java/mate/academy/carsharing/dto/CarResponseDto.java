package mate.academy.carsharing.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.model.Car;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CarResponseDto {
    private Long id;
    private String model;
    private String brand;
    private Car.CarType type;
    private int inventory;
    private BigDecimal dailyFee;
}
