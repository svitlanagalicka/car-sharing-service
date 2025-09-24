package mate.academy.carsharing.service;

import mate.academy.carsharing.dto.RentalRequestDto;
import mate.academy.carsharing.dto.RentalResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RentalService {
    RentalResponseDto create(RentalRequestDto rentalRequestDto);

    RentalResponseDto returnCar(Long rentalId);

    RentalResponseDto getById(Long id);

    Page<RentalResponseDto> findAllByUserId(Pageable pageable, Long userId, Boolean isActive);

    Page<RentalResponseDto> findAll(Pageable pageable);
}
