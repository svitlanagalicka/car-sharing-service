package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.CarRequestDto;
import mate.academy.carsharing.dto.CarResponseDto;
import mate.academy.carsharing.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CarMapper {

    Car toModel(CarRequestDto carRequestDto);

    CarResponseDto toDto(Car car);

    Car updateCar(@MappingTarget Car car, CarRequestDto carRequestDto);
}
