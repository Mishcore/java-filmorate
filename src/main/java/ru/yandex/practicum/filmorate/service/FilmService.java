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
        if (count < 1) {
            throw new IllegalArgumentException("Illegal count value");
        }
        return filmStorage.getPopular(count);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
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
}
