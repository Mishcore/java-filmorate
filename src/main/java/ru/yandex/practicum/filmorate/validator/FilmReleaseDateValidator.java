package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private static final LocalDate CINEMA_BIRTH_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return (!value.isBefore(CINEMA_BIRTH_DATE) && !value.isAfter(LocalDate.now()));
    }

}
