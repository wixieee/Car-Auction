package edu.lpnu.auction.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CarConfigurationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCarConfiguration {
    String message() default "Некоректна конфігурація авто";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}