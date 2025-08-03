package mate.academy.carsharing.service;

import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.CarRequestDto;
import mate.academy.carsharing.dto.CarResponseDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.mapper.CarMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.CarRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public CarResponseDto save(CarRequestDto carRequestDto) {
        Car car = carMapper.toModel(carRequestDto);
        Car savedCar = carRepository.save(car);
        return carMapper.toDto(savedCar);
    }

    @Override
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carRepository.findAll(pageable).stream()
                .map(carMapper::toDto)
                .toList();
    }

    @Override
    public CarResponseDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Car not found with id " + id));
        return carMapper.toDto(car);
    }

    @Override
    public void deleteById(Long id) {
        if (carRepository.existsById(id)) {
            carRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Can not find car with id: " + id);
        }
    }

    @Override
    public CarResponseDto updateCar(Long id, CarRequestDto carRequestDto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cant find car by id " + id));
        carMapper.updateCar(car, carRequestDto);
        Car updatedCar = carRepository.save(car);
        return carMapper.toDto(updatedCar);
    }
}
