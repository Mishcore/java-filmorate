package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(int id, long userId) {
        Film film = filmStorage.getFilm(id);
        film.addLike();
        userStorage.getUser(userId).getLikedFilms().add(id);
        log.info("Like has been added");
    }

    public void deleteLike(int id, long userId) {
        Film film = filmStorage.getFilm(id);
        film.deleteLike();
        userStorage.getUser(userId).getLikedFilms().remove(id);
        log.info("Like has been deleted");
    }

    public List<Film> getPopular(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("Illegal count value");
        }
        if (count == 1) {
            log.info("The most popular film requested");
        } else {
            log.info(count + " most popular films requested");
        }
        return filmStorage.getPopular(count);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }
}
