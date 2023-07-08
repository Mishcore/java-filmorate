package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    public List<Film> getAllFilms() {
        log.info("Films list requested");
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        log.info("Film requested");
        if (id <= 0) {
            throw new EntityNotFoundException("Invalid Film ID");
        }
        return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(int id) {
        if (id <= 0) {
            throw new EntityNotFoundException("Invalid Film ID");
        }
        filmStorage.deleteFilm(id);
    }

    public void addLike(int filmId, long userId) {
        if (filmId <= 0) {
            throw new EntityNotFoundException("Invalid Film ID");
        }
        if (userId <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, long userId) {
        if (filmId <= 0) {
            throw new EntityNotFoundException("Invalid Film ID");
        }
        if (userId <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Illegal count value");
        } else if (count == 1) {
            log.info("The most popular film requested");
        } else {
            log.info(count + " most popular films requested");
        }
        return filmStorage.getPopular(count);
    }
}
