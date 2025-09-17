package mate.academy.carsharing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class RentalServiceImplTest {
    @Mock
    private RentalRepository rentalRepository;
    @Mock
    private RentalMapper rentalMapper;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private RentalServiceImpl rentalService;

    @BeforeEach
    void setUp() {
        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("user@mail.com");
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Successfully creates an rental")
    void create_success() {
        Car car = new Car();
        car.setId(1L);
        car.setInventory(2);

        User user = new User();
        user.setId(5L);
        user.setEmail("user@mail.com");

        RentalRequestDto requestDto = new RentalRequestDto(1L, LocalDate.now().plusDays(3));
        Rental rental = new Rental();
        rental.setId(10L);
        rental.setCar(car);
        rental.setUser(user);
        rental.setRentalDate(LocalDate.now());
        rental.setReturnDate(requestDto.returnDate());

        RentalResponseDto responseDto = new RentalResponseDto(10L,
                rental.getRentalDate(),
                rental.getReturnDate(),
                null,
                car.getId(),
                user.getId());
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalMapper.toRentalDto(rental)).thenReturn(responseDto);

        RentalResponseDto result = rentalService.create(requestDto);

        assertEquals(10L, result.id());
        assertEquals(1, car.getInventory());
        verify(carRepository).save(car);
        verify(notificationService).sendNotification(contains("New rental created with ID"));
    }

    @Test
    @DisplayName("Throws exception when creating rental with no inventory")
    void create_throwsException_whenCarNotFound() {
        Car car = new Car();
        car.setId(1L);
        car.setInventory(0);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        RentalRequestDto rentalRequestDto = new RentalRequestDto(1L, LocalDate.now().plusDays(3));

        assertThrows(EntityNotFoundException.class,
                () -> rentalService.create(rentalRequestDto));
    }

    @Test
    @DisplayName("Successfully returns a rental and updates car inventory")
    void returnCar_success() {
        Car car = new Car();
        car.setId(1L);
        car.setInventory(0);

        Rental rental = new Rental();
        rental.setId(13L);
        rental.setCar(car);
        rental.setActualReturnDate(null);

        RentalResponseDto rentalResponseDto = new RentalResponseDto(13L,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDate.now(),
                1L,
                1L);

        when(rentalRepository.findById(13L)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(rental)).thenReturn(rental);
        when(carRepository.save(car)).thenReturn(car);
        when(rentalMapper.toRentalDto(rental)).thenReturn(rentalResponseDto);

        RentalResponseDto result = rentalService.returnCar(13L);

        assertNotNull(rental.getActualReturnDate());
        assertEquals(1, car.getInventory());
        verify(carRepository).save(car);
        verify(rentalRepository).save(rental);
    }

    @Test
    @DisplayName("Throws exception when returning a car already returned")
    void returnCar_throwsException_whenCarAlreadyReturned() {
        Rental rental = new Rental();
        rental.setId(10L);
        rental.setActualReturnDate(LocalDate.now());
        when(rentalRepository.findById(10L)).thenReturn(Optional.of(rental));
        assertThrows(EntityNotFoundException.class,
                () -> rentalService.returnCar(10L));
    }

    @Test
    @DisplayName("Successfully gets rental by ID")
    void getById_success() {
        Rental rental = new Rental();
        rental.setId(11L);
        RentalResponseDto rentalResponseDto = new RentalResponseDto(11L,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                null,
                1L,
                1L);
        when(rentalRepository.findById(11L)).thenReturn(Optional.of(rental));
        when(rentalMapper.toRentalDto(rental)).thenReturn(rentalResponseDto);
        RentalResponseDto result = rentalService.getById(11L);
        assertEquals(11L, result.id());
    }

    @Test
    @DisplayName("Throws exception when rental not found by ID")
    void getById_throwsException_whenRentalNotFound() {
        when(rentalRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RentalNotFoundException.class,
                () -> rentalService.getById(999L));
    }

    @Test
    @DisplayName("Successfully finds rentals by user ID")
    void findAllByUserId_success() {
        Page<Rental> rentals = new PageImpl<>(List.of(new Rental(), new Rental(), new Rental()));
        when(rentalRepository.findAllByUserId(7L, PageRequest.of(0,10))).thenReturn(rentals);
        when(rentalMapper.toRentalDto(any(Rental.class))).thenReturn(new RentalResponseDto(1L,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                null,
                1L,
                7L));
        Page<RentalResponseDto> result = rentalService.findAllByUserId(PageRequest.of(0, 10),
                7L,
                null);
        assertEquals(3, result.getContent().size());
    }

    @Test
    @DisplayName("Returns empty list when no rentals found for user")
    void findAllByUserId_emptyResult() {
        Page<Rental> rentals = new PageImpl<>(List.of());
        when(rentalRepository.findAllByUserId(7L, PageRequest.of(0, 10))).thenReturn(rentals);

        Page<RentalResponseDto> result = rentalService.findAllByUserId(PageRequest.of(0, 10),
                7L,
                null);

        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Returns only active rentals when isActive=true")
    void findAllByUserId_actualReturnDateIsNull() {
        Page<Rental> rentals = new PageImpl<>(List.of());
        when(rentalRepository.findAllByUserIdAndActualReturnDateIsNull(7L,
                PageRequest.of(0, 10))).thenReturn(rentals);

        Page<RentalResponseDto> result = rentalService.findAllByUserId(PageRequest.of(0, 10),
                7L,
                true);

        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Returns only inactive rentals when isActive=false")
    void findAllByUserId_actualReturnDateIsNotNull() {
        Page<Rental> rentals = new PageImpl<>(List.of());
        when(rentalRepository.findAllByUserIdAndActualReturnDateIsNotNull(7L,
                PageRequest.of(0, 10))).thenReturn(rentals);

        Page<RentalResponseDto> result = rentalService.findAllByUserId(PageRequest.of(0, 10),
                7L,
                false);

        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Successfully finds all rentals")
    void findAll_success() {
        Page<Rental> rentals = new PageImpl<>(List.of(new Rental(), new Rental()));
        when(rentalRepository.findAll(PageRequest.of(0, 10))).thenReturn(rentals);
        when(rentalMapper.toRentalDto(any(Rental.class))).thenReturn(new RentalResponseDto(1L,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                null,
                1L,
                5L));
        Page<RentalResponseDto> result = rentalService.findAll(PageRequest.of(0, 10));
        assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("Returns empty list when no rentals exist")
    void findAll_emptyResult() {
        Page<Rental> rentals = new PageImpl<>(List.of());
        when(rentalRepository.findAll(PageRequest.of(0, 10))).thenReturn(rentals);

        Page<RentalResponseDto> result = rentalService.findAll(PageRequest.of(0, 10));

        assertTrue(result.getContent().isEmpty());
    }
}
