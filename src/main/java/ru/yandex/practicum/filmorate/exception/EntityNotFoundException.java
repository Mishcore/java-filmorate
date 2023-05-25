package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends IllegalArgumentException {
    public EntityNotFoundException(Object o) {
        super(o.getClass().getSimpleName() + "not found");
    }
}
