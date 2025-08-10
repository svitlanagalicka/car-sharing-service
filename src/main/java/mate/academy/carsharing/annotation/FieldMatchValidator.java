package mate.academy.carsharing.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import mate.academy.carsharing.dto.UserRequestDto;

public class FieldMatchValidator implements
        ConstraintValidator<FieldMatch, UserRequestDto> {
    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRequestDto userRequestDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (userRequestDto == null) {
            return true;
        }
        return userRequestDto.getPassword().equals(userRequestDto.getRepeatPassword());
    }
}
