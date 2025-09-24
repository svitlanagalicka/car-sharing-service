package mate.academy.carsharing.repository.car;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.repository.SpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class BrandSpecificationProvider implements SpecificationProvider<Car> {
    private static final String BRAND_COLUMN = "brand";

    @Override
    public String getKey() {
        return BRAND_COLUMN;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return new Specification<Car>() {
            @Override
            public Predicate toPredicate(Root<Car> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return root.get(BRAND_COLUMN)
                        .in(Arrays.stream(params).toArray());
            }
        };
    }
}
