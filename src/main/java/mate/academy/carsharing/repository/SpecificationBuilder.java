package mate.academy.carsharing.repository;

import mate.academy.carsharing.dto.CarSearchParametersDto;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationBuilder<T> {
    Specification<T> build(CarSearchParametersDto searchParametersDto);
}
