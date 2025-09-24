package mate.academy.carsharing.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    @Query("SELECT r FROM Rental r JOIN FETCH r.car WHERE r.id = :id AND r.isDeleted = false")
    Optional<Rental> findByIdWithCar(@Param("id") Long id);

    Page<Rental> findAllByUserId(Long userId, Pageable pageable);

    Page<Rental> findAllByUserIdAndActualReturnDateIsNull(Long userId, Pageable pageable);

    Page<Rental> findAllByUserIdAndActualReturnDateIsNotNull(Long userId, Pageable pageable);

    List<Rental> findAllByReturnDateBeforeAndActualReturnDateIsNull(LocalDate date);
}
