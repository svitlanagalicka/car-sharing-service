package mate.academy.carsharing.service;

import com.stripe.model.checkout.Session;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;

import java.math.BigDecimal;

public interface StripePaymentService {
    Session createStripeSession(Payment payment, Rental rental, BigDecimal amount);

    void attachSessionToPayment(Payment payment, Session session);
}
