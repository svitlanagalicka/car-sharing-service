package mate.academy.carsharing.service;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;

public interface StripePaymentService {
    Session createStripeSession(Payment payment, Rental rental, BigDecimal amount);

    void attachSessionToPayment(Payment payment, Session session);
}
