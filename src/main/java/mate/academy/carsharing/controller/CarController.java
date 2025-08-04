package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.CarRequestDto;
import mate.academy.carsharing.dto.CarResponseDto;
import mate.academy.carsharing.dto.CarSearchParametersDto;
import mate.academy.carsharing.service.CarService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cars API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @GetMapping
    public List<CarResponseDto> findAll(Pageable pageable) {
        return carService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public CarResponseDto getCarById(@PathVariable Long id) {
        return carService.getCarById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDto save(@RequestBody @Valid CarRequestDto carRequestDto) {
        return carService.save(carRequestDto);
    }

    @PutMapping("/{id}")
    public CarResponseDto updateCar(@PathVariable Long id,
                                    @RequestBody CarRequestDto updatedCar) {
        return carService.updateCar(id, updatedCar);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        carService.deleteById(id);
    }

    @GetMapping("/search")
    public List<CarResponseDto> searchCars(CarSearchParametersDto searchParametersDto) {
        return carService.search(searchParametersDto);
    }
}
