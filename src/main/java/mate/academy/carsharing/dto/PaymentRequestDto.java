package mate.academy.carsharing.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.model.Payment;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PaymentRequestDto {
    @NotNull
    private Long rentalId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Payment.Type type;
}
