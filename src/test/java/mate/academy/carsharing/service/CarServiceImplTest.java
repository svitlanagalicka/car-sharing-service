package mate.academy.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.dto.CarRequestDto;
import mate.academy.carsharing.dto.CarResponseDto;
import mate.academy.carsharing.dto.CarSearchParametersDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.mapper.CarMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.CarRepository;
import mate.academy.carsharing.repository.CarSpecificationBuilder;
import mate.academy.carsharing.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @Mock
    private CarSpecificationBuilder carSpecificationBuilder;
    @InjectMocks
    private CarServiceImpl carService;

    @Test
    @DisplayName("Saves the car if the entered data is correct")
    void save_returnSavedCar_whenInputIsValid() {
        Car car = TestUtil.createCar();
        Car savedCar = new Car();
        savedCar.setId(1L);
        savedCar.setModel(car.getModel());
        savedCar.setBrand(car.getBrand());
        savedCar.setType(car.getType());
        savedCar.setInventory(car.getInventory());
        savedCar.setDailyFee(car.getDailyFee());

        CarRequestDto carRequestDto = TestUtil.carRequestDto();
        CarResponseDto carResponseDto = TestUtil.carResponseDto(1L);

        when(carMapper.toModel(carRequestDto)).thenReturn(car);
        when(carRepository.save(car)).thenReturn(savedCar);
        when(carMapper.toDto(savedCar)).thenReturn(carResponseDto);

        CarResponseDto result = carService.save(carRequestDto);
        assertEquals(carResponseDto, result);
        verify(carMapper).toModel(carRequestDto);
        verify(carRepository).save(car);
        verify(carMapper).toDto(savedCar);
    }

    @Test
    @DisplayName("Returns list of car DTOs for findAll")
    void findAll_returnListOfCarResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Car car1 = new Car();
        car1.setId(1L);
        Car car2 = new Car();
        car2.setId(2L);
        List<Car> cars = List.of(car1, car2);
        Page<Car> carPage = new PageImpl<>(cars, pageable, cars.size());

        CarResponseDto dto1 = new CarResponseDto();
        CarResponseDto dto2 = new CarResponseDto();

        when(carRepository.findAll(pageable)).thenReturn(carPage);
        when(carMapper.toDto(car1)).thenReturn(dto1);
        when(carMapper.toDto(car2)).thenReturn(dto2);

        List<CarResponseDto> result = carService.findAll(pageable);
        assertEquals(List.of(dto1, dto2), result);
        verify(carRepository).findAll(pageable);
        verify(carMapper).toDto(car1);
        verify(carMapper).toDto(car2);
    }

    @Test
    @DisplayName("Returns car DTO when car exists by id")
    void getCarById_returnCarResponseDto_whenCarExist() {
        Long id = 1L;
        Car car = TestUtil.createCar();
        car.setId(id);

        CarResponseDto carResponseDto = TestUtil.carResponseDto(id);

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        when(carMapper.toDto(car)).thenReturn(carResponseDto);
        CarResponseDto result = carService.getCarById(id);
        assertEquals(carResponseDto, result);
        verify(carRepository).findById(id);
        verify(carMapper).toDto(car);
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when car not found by id")
    void getCarById_throwEntityNotFoundException_whenCarNotFound() {
        Long id = 999L;
        when(carRepository.findById(id)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> carService.getCarById(id));
        assertTrue(exception.getMessage().contains("Car not found with id 999"));
        verify(carRepository).findById(id);
    }

    @Test
    @DisplayName("Deletes car successfully when car exists")
    void deleteById_deleteCar_whenCarExist() {
        Long id = 1L;
        when(carRepository.existsById(id)).thenReturn(true);
        carService.deleteById(id);
        verify(carRepository).existsById(id);
        verify(carRepository).deleteById(id);
    }

    @Test
    @DisplayName("Throws EntityNotFoundException when deleting non-existing car")
    void deleteById_throwEntityNotFoundException_whenCarNotExist() {
        Long id = 999L;
        when(carRepository.existsById(id)).thenReturn(false);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> carService.deleteById(id));
        assertTrue(exception.getMessage().contains("Can not find car with id: 999"));
        verify(carRepository).existsById(id);
        verify(carRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Updates car and returns updated DTO when car exists")
    void updateCar_returnUpdatedCarDto_whenCarExist() {
        CarRequestDto carRequestDto = new CarRequestDto();
        carRequestDto.setModel("KIA");
        carRequestDto.setBrand("SOUL - Updated");
        carRequestDto.setType(Car.CarType.valueOf("HATCHBACK"));
        carRequestDto.setInventory(7);
        carRequestDto.setDailyFee(BigDecimal.valueOf(199));
        Long id = 1L;

        Car car = new Car();
        car.setId(id);
        car.setModel("KIA");
        car.setBrand("SOUL");
        car.setType(Car.CarType.valueOf("HATCHBACK"));
        car.setInventory(7);
        car.setDailyFee(BigDecimal.valueOf(210));

        CarResponseDto updatedCarDto = new CarResponseDto();

        when(carRepository.findById(id)).thenReturn(Optional.of(car));
        when(carRepository.save(car)).thenReturn(car);
        when(carMapper.toDto(car)).thenReturn(updatedCarDto);

        CarResponseDto result = carService.updateCar(id, carRequestDto);
        assertEquals(updatedCarDto, result);
        verify(carRepository).findById(id);
        verify(carMapper).updateCar(car, carRequestDto);
        verify(carRepository).save(car);
        verify(carMapper).toDto(car);
    }

    @Test
    @DisplayName("Searches cars with given parameters and returns DTO list")
    void search_returnListOfCars() {
        CarSearchParametersDto parametersDto = new CarSearchParametersDto(null, null);
        Specification<Car> specification = mock(Specification.class);
        List<Car> cars = List.of(new Car(), new Car(), new Car());
        List<CarResponseDto> carResponseDtos = List.of(new CarResponseDto(),
                new CarResponseDto(),
                new CarResponseDto());

        when(carSpecificationBuilder.build(parametersDto)).thenReturn(specification);
        when(carRepository.findAll(specification)).thenReturn(cars);
        when(carMapper.toDto(any(Car.class)))
                .thenReturn(carResponseDtos.get(0), carResponseDtos.get(1), carResponseDtos.get(2));
        List<CarResponseDto> result = carService.search(parametersDto);
        assertEquals(carResponseDtos, result);
        verify(carSpecificationBuilder).build(parametersDto);
        verify(carRepository).findAll(specification);
    }
}
