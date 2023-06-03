package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.EntityValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        log.info("Film list requested");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(++id);
        films.put(id, film);
        log.info("Film added");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Invalid film (request body has no film id)");
            throw new EntityValidationException("Invalid film (request body has no film id)");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Film not found");
            throw new EntityNotFoundException(Film.class.getSimpleName());
        }
        films.replace(film.getId(), film);
        log.info("Film info updated");
        return film;
    }

    @Override
    public void deleteFilm(int filmId) {
        if (!films.containsKey(filmId)) {
            log.warn("Film not found");
            throw new EntityNotFoundException(Film.class.getSimpleName());
        }
        films.remove(filmId);
        log.info("Film deleted");
    }
}
