package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
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
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        validateFilmId(id);
        return films.get(id);
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
        validateFilmId(film.getId());
        films.replace(film.getId(), film);
        log.info("Film info updated");
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        validateFilmId(id);
        films.remove(id);
        log.info("Film deleted");
    }

    private void validateFilmId(int filmId) {
        if (filmId <= 0) {
            log.warn("Invalid film ID");
            throw new EntityNotFoundException("Invalid film ID");
        }
        if (!films.containsKey(filmId)) {
            log.warn("Film not found");
            throw new EntityNotFoundException("Film not found");
        }
    }
}
