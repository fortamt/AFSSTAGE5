package antifraud.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RegionCodesValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegionCodesConstraint {
    String message() default "Invalid region code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}