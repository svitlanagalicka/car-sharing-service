package mate.academy.carsharing.service;

import java.util.List;
import mate.academy.carsharing.dto.CarRequestDto;
import mate.academy.carsharing.dto.CarResponseDto;
import mate.academy.carsharing.dto.CarSearchParametersDto;
import org.springframework.data.domain.Pageable;

public interface CarService {

    CarResponseDto save(CarRequestDto carRequestDto);

    List<CarResponseDto> findAll(Pageable pageable);

    CarResponseDto getCarById(Long id);

    void deleteById(Long id);

    CarResponseDto updateCar(Long id, CarRequestDto carRequestDto);

    List<CarResponseDto> search(CarSearchParametersDto parametersDto);
}
