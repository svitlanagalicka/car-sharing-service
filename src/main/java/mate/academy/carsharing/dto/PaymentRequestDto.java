package mate.academy.carsharing.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import mate.academy.carsharing.model.Payment;

@Data
public class PaymentRequestDto {
    @NotNull
    private Long rentalId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Payment.Type type;
}
