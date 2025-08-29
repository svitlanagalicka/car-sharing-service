package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.RentalRequestDto;
import mate.academy.carsharing.dto.RentalResponseDto;
import mate.academy.carsharing.model.Rental;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface RentalMapper {
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "user.id", target = "userId")
    RentalResponseDto toRentalDto(Rental rental);

    Rental toEntity(RentalRequestDto dto);

    void update(@MappingTarget Rental rental, RentalRequestDto rentalRequestDto);
}
