package ru.yandex.practicum.filmorate.storage.impl.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final UserStorage userStorage;

    @Override
    public List<Film> getAllFilms() {
        log.info("Films list requested");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        validateFilmId(id);
        log.info("Film requested");
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
        if (count == 1) {
            log.info("The most popular film requested");
        } else {
            log.info(count + " most popular films requested");
        }
        return films.values().stream().sorted(new FilmComparator()).limit(count).collect(Collectors.toList());
    }

    @Override
    public void addLike(int filmId, long userId) {
        User user = userStorage.getUser(userId);
        films.get(filmId).addLike(user.getId());
        log.info("Like added");
    }

    @Override
    public void deleteLike(int filmId, long userId) {
        User user = userStorage.getUser(userId);
        films.get(filmId).deleteLike(user.getId());
        log.info("Like deleted");
    }

    private void validateFilmId(int filmId) {
        if (filmId <= 0) {
            throw new EntityNotFoundException("Invalid film ID");
        }
        if (!films.containsKey(filmId)) {
            throw new EntityNotFoundException("Film not found");
        }
    }

    static class FilmComparator implements Comparator<Film> {

        @Override
        public int compare(Film o1, Film o2) {
            int compared = o2.getRate() - o1.getRate();
            if (compared == 0) {
                return o2.getId() - o1.getId();
            } else {
                return compared;
            }
        }
    }
}
