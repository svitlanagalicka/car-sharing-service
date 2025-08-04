package mate.academy.carsharing.repository;

import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.CarSearchParametersDto;
import mate.academy.carsharing.model.Car;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CarSpecificationBuilder implements SpecificationBuilder<Car> {
    private static final String BRAND_COLUMN = "brand";
    private static final String MODEL_COLUMN = "model";
    private final SpecificationProviderManager<Car> carSpecificationProviderManager;

    @Override
    public Specification<Car> build(CarSearchParametersDto searchParametersDto) {
        Specification<Car> specification = Specification.where(null);
        if (searchParametersDto.model() != null && searchParametersDto.model().length > 0) {
            specification = specification
                    .and(carSpecificationProviderManager.getSpecificationProvider(MODEL_COLUMN)
                            .getSpecification(searchParametersDto.model()));
        }
        if (searchParametersDto.brand() != null && searchParametersDto.brand().length > 0) {
            specification = specification
                    .and(carSpecificationProviderManager.getSpecificationProvider(BRAND_COLUMN)
                            .getSpecification(searchParametersDto.brand()));
        }
        return specification;
    }
}
