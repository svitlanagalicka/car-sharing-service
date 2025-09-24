package mate.academy.carsharing.repository;

import java.util.Optional;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findAllByRentalUserId(Long userId, Pageable pageable);

    Optional<Payment> findBySessionId(String sessionId);

    Page<Payment> findAllByRentalId(Long rentalId, Pageable pageable);

    Page<Payment> findAllByRentalAndStatus(Rental rental, Payment.Status status, Pageable pageable);
}
