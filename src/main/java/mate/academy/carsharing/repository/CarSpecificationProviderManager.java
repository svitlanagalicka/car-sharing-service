package mate.academy.carsharing.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.model.Car;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CarSpecificationProviderManager implements SpecificationProviderManager<Car> {
    private final List<SpecificationProvider<Car>> specificationProviders;

    @Override
    public SpecificationProvider<Car> getSpecificationProvider(String key) {
        return specificationProviders.stream()
                .filter(p -> p.getKey().equals(key))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Can not find correct specification provider for key"
                                + key));
    }
}
