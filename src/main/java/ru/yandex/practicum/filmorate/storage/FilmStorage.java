package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film getFilm(int id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int filmId);

    List<Film> getPopular(int count);

    void addLike(int filmId, long userId);

    void deleteLike(int filmId, long userId);
}