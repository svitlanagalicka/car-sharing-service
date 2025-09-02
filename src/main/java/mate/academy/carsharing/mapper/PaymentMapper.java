package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.PaymentRequestDto;
import mate.academy.carsharing.dto.PaymentResponseDto;
import mate.academy.carsharing.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    Payment toEntity(PaymentRequestDto paymentRequestDto);

    PaymentResponseDto toPaymentDto(Payment payment);
}
