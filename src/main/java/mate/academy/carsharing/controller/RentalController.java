package mate.academy.carsharing.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.RentalRequestDto;
import mate.academy.carsharing.dto.RentalResponseDto;
import mate.academy.carsharing.service.RentalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService rentalService;

    @PostMapping
    @Operation(summary = "Save a new rental", description = "Create a new rental entry")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponseDto createRental(@RequestBody @Valid RentalRequestDto rentalRequestDto) {
        return rentalService.create(rentalRequestDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a rental by its ID",
            description = "Get detailed information about a rental by its ID")
    public RentalResponseDto getRentalById(@PathVariable Long id) {
        return rentalService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Get all rentals",
            description = "Get a list of all available rentals")
    public Page<RentalResponseDto> getRentals(Pageable pageable,
                                              @RequestParam Long userId,
                                              @RequestParam boolean isActive) {
        return rentalService.findAllByUserId(pageable, userId, isActive);
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return rental",
            description = "Return information about rental by ID")
    public RentalResponseDto returnRental(@PathVariable Long id) {
        return rentalService.returnCar(id);
    }
}
