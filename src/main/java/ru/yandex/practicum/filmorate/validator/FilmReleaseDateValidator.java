package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FilmReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private String earliestDate;

    @Override
    public void initialize(ValidReleaseDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        earliestDate = constraintAnnotation.earliestDate();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return (!value.isBefore(LocalDate.parse(earliestDate, DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                && !value.isAfter(LocalDate.now()));
    }

}
