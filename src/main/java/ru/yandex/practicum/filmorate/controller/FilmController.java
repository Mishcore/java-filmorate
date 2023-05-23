package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Movie list requested");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        film.setId(++id);
        films.put(id, film);
        log.info("Movie added");
        return ResponseEntity.status(HttpStatus.CREATED).body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Invalid movie (request body has no movie id)");
            throw new InvalidFilmException();
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Movie not found");
            throw new InvalidFilmIdException();
        }
        films.replace(film.getId(), film);
        log.info("Movie info updated");
        return ResponseEntity.ok(film);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    static class InvalidFilmException extends IllegalArgumentException {}

    @ResponseStatus(HttpStatus.NOT_FOUND)
    static class InvalidFilmIdException extends IllegalArgumentException {}
}
