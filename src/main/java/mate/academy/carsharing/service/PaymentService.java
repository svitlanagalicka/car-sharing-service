package mate.academy.carsharing.service;

import mate.academy.carsharing.dto.PaymentResponseDto;
import mate.academy.carsharing.dto.RentalResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponseDto createPayment(Long rentalId);

    RentalResponseDto getPayment(Long paymentId);

    Page<PaymentResponseDto> getPaymentsByUserId(Long userId, Pageable pageable);
}
