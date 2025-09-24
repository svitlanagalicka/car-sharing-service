package mate.academy.carsharing.util;

import java.math.BigDecimal;
import mate.academy.carsharing.dto.CarRequestDto;
import mate.academy.carsharing.dto.CarResponseDto;
import mate.academy.carsharing.model.Car;

public class TestUtil {

    public static CarRequestDto carRequestDto() {
        CarRequestDto carRequestDto = new CarRequestDto();
        carRequestDto.setModel("KIA");
        carRequestDto.setBrand("SOUL");
        carRequestDto.setType(Car.CarType.valueOf("HATCHBACK"));
        carRequestDto.setInventory(7);
        carRequestDto.setDailyFee(new BigDecimal("199.00"));
        return carRequestDto;
    }

    public static CarResponseDto carResponseDto(Long id) {
        CarRequestDto carRequestDto = carRequestDto();
        CarResponseDto expected = new CarResponseDto();
        expected.setId(id);
        expected.setModel(carRequestDto.getModel());
        expected.setBrand(carRequestDto.getBrand());
        expected.setType(carRequestDto.getType());
        expected.setInventory(carRequestDto.getInventory());
        expected.setDailyFee(carRequestDto.getDailyFee());
        return expected;
    }

    public static Car createCar() {
        Car car = new Car();
        car.setModel("KIA");
        car.setBrand("SOUL");
        car.setType((Car.CarType.valueOf("HATCHBACK")));
        car.setInventory(7);
        car.setDailyFee(BigDecimal.valueOf(199));
        return car;
    }
}
