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
public class ModelSpecificationProvider implements SpecificationProvider<Car> {
    private static final String MODEL_COLUMN = "model";

    @Override
    public String getKey() {
        return MODEL_COLUMN;
    }

    @Override
    public Specification<Car> getSpecification(String[] params) {
        return new Specification<Car>() {
            @Override
            public Predicate toPredicate(Root<Car> root,
                                         CriteriaQuery<?> query,
                                         CriteriaBuilder criteriaBuilder) {
                return root.get(MODEL_COLUMN)
                        .in(Arrays.stream(params).toArray());
            }
        };
    }
}
