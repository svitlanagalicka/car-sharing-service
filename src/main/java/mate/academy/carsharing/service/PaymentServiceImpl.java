package mate.academy.carsharing.service;

import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.PaymentResponseDto;
import mate.academy.carsharing.dto.RentalResponseDto;
import mate.academy.carsharing.mapper.PaymentMapper;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.repository.PaymentRepository;
import mate.academy.carsharing.repository.RentalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StripePaymentServiceImpl stripePaymentService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(Long rentalId) {
        Rental rental = rentalRepository.findByIdWithCar(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));
        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setAmount(rental.getTotalPrice());
        payment.setType(Payment.Type.PAYMENT);
        payment.setStatus(Payment.Status.PENDING);

        Session session = stripePaymentService.createStripeSession(payment,
                rental,
                rental.getTotalPrice());
        stripePaymentService.attachSessionToPayment(payment, session);

        paymentRepository.save(payment);
        notificationService.sendNotification("New payment was created for rental with ID: "
                + rental);
        return paymentMapper.toPaymentDto(payment);
    }

    @Override
    public RentalResponseDto getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        Rental rental = payment.getRental();
        return new RentalResponseDto(rental.getId(),
                rental.getRentalDate(),
                rental.getReturnDate(),
                rental.getActualReturnDate(),
                rental.getCar().getId(),
                rental.getUser().getId());
    }

    @Override
    public Page<PaymentResponseDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findAllByRentalUserId(userId, pageable)
                .map(paymentMapper::toPaymentDto);
    }
}
