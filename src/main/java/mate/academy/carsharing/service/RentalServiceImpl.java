package mate.academy.carsharing.service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.RentalRequestDto;
import mate.academy.carsharing.dto.RentalResponseDto;
import mate.academy.carsharing.exception.EntityNotFoundException;
import mate.academy.carsharing.exception.RentalNotFoundException;
import mate.academy.carsharing.mapper.RentalMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.CarRepository;
import mate.academy.carsharing.repository.RentalRepository;
import mate.academy.carsharing.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {
    private final RentalRepository rentalRepository;
    private final RentalMapper rentalMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public RentalResponseDto create(RentalRequestDto rentalRequestDto) {
        Car car = carRepository.findById(rentalRequestDto.carId()).orElseThrow(()
                -> new EntityNotFoundException("Can not found car with id: "
                + rentalRequestDto.carId()));
        if (car.getInventory() <= 0) {
            throw new EntityNotFoundException("No cars available for this model: "
                    + car.getModel());
        }
        car.setInventory(car.getInventory() - 1);
        carRepository.save(car);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(()
                -> new EntityNotFoundException("Can not found user with email: " + email));

        Rental rental = new Rental();
        rental.setCar(car);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(rentalRequestDto.returnDate());

        rentalRepository.save(rental);
        notificationService.sendNotification("New rental created with ID: "
                + rental.getId()
                + ", User ID: " + rental.getUser().getId()
                + ", Car ID: " + rental.getCar().getId());
        return rentalMapper.toRentalDto(rental);
    }

    @Override
    @Transactional
    public RentalResponseDto returnCar(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() ->
                        new RentalNotFoundException("Rental not found with id: " + rentalId));
        if (rental.getActualReturnDate() != null) {
            throw new EntityNotFoundException("Car for rental id " + rentalId + "was returned");
        }
        rental.setActualReturnDate(LocalDate.now());
        Car car = rental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);
        rentalRepository.save(rental);
        return rentalMapper.toRentalDto(rental);
    }

    @Override
    public RentalResponseDto getById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException("Rental not found with id: " + id));
        return rentalMapper.toRentalDto(rental);
    }

    @Override
    @Transactional
    public Page<RentalResponseDto> findAllByUserId(Pageable pageable,
                                                   Long userId,
                                                   Boolean isActive) {
        Page<Rental> rentals;
        if (isActive == null) {
            rentals = rentalRepository.findAllByUserId(userId, pageable);
        } else if (isActive) {
            rentals = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNull(userId, pageable);
        } else {
            rentals = rentalRepository
                    .findAllByUserIdAndActualReturnDateIsNotNull(userId, pageable);
        }
        return rentals.map(rentalMapper::toRentalDto);
    }

    @Override
    public Page<RentalResponseDto> findAll(Pageable pageable) {
        return rentalRepository.findAll(pageable)
                .map(rentalMapper::toRentalDto);
    }
}
