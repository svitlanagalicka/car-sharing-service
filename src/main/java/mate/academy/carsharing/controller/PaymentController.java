package mate.academy.carsharing.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.PaymentResponseDto;
import mate.academy.carsharing.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponseDto createPayment(@RequestParam Long rentalId) {
        return paymentService.createPayment(rentalId);
    }

    @GetMapping
    public Page<PaymentResponseDto> getPaymentsByUser(@RequestParam Long userId,
                                                      Pageable pageable) {
        return paymentService.getPaymentsByUserId(userId, pageable);
    }

    @GetMapping("success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId) {
        return "Payment successful! Session ID: " + sessionId;
    }

    @GetMapping("cancel")
    public String paymentCanceled(@RequestParam("session_id") String sessionId) {
        return "Payment canceled! Session ID: " + sessionId;
    }
}
