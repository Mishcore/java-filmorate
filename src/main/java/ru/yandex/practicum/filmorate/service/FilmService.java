package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Set<Film> sortedFilms = new TreeSet<>(FilmService::compareByLikes);

    public void addLike(int id, int userId) {
        filmStorage.getFilm(id).getLikers().add(userStorage.getUser(userId));
        log.info("Like has been added");
    }

    public void deleteLike(int id, int userId) {
        filmStorage.getFilm(id).getLikers().remove(userStorage.getUser(userId));
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
        return sortedFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public Film addFilm(Film film) {
        filmStorage.addFilm(film);
        sortedFilms.add(film);
        return film;
    }

    public Film updateFilm(Film film) {
        sortedFilms.remove(film);
        Film updatedFilm = filmStorage.updateFilm(film);
        sortedFilms.add(updatedFilm);
        return updatedFilm;
    }

    public void deleteFilm(int id) {
        sortedFilms.remove(filmStorage.getFilm(id));
        filmStorage.deleteFilm(id);
    }

    private static int compareByLikes(Film o1, Film o2) {
        int compared = o2.getLikers().size() - o1.getLikers().size();
        if (compared == 0) {
            return o2.getId() - o1.getId();
        } else {
            return compared;
        }
    }
}
