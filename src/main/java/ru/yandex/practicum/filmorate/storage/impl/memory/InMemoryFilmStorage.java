package ru.yandex.practicum.filmorate.storage.impl.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public List<Film> getPopular(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingInt(Film::getRate))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmId(int filmId) {
        if (filmId <= 0) {
            throw new EntityNotFoundException("Invalid film ID");
        }
        if (!films.containsKey(filmId)) {
            throw new EntityNotFoundException("Film not found");
        }
    }
}
