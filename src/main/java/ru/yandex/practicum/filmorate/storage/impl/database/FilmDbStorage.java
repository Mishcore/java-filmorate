package ru.yandex.practicum.filmorate.storage.impl.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Primary
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAllFilms() {
        log.info("Films list requested");
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa_rating_id," +
                " mpa.name AS mpa, GROUP_CONCAT(fg.genre_id) AS genre_id, GROUP_CONCAT(g.name) AS genre" +
                " FROM films AS f" +
                " JOIN mpa_ratings AS mpa ON f.mpa_rating_id = mpa.id" +
                " LEFT JOIN film_genres AS fg ON f.id = fg.film_id" +
                " LEFT JOIN genres AS g ON fg.genre_id = g.id" +
                " GROUP BY f.id";
        return jdbcTemplate.query(sqlQuery, filmRowMapper());
    }

    @Override
    public Film getFilm(int id) {
        log.info("Film requested");
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa_rating_id," +
                " mpa.name AS mpa, GROUP_CONCAT(fg.genre_id) AS genre_id, GROUP_CONCAT(g.name) AS genre" +
                " FROM films AS f" +
                " JOIN mpa_ratings AS mpa ON f.mpa_rating_id = mpa.id" +
                " LEFT JOIN film_genres AS fg ON f.id = fg.film_id" +
                " LEFT JOIN genres AS g ON fg.genre_id = g.id" +
                " WHERE f.id = ?" +
                " GROUP BY f.id";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, filmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Film not found");
        }
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        film.setId(simpleJdbcInsert.executeAndReturnKey(filmToMap(film)).intValue());

        String sqlQueryForGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sqlQueryForGenres, new FilmBatchPreparedStatementSetter(film));
        log.info("Film added");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_rating_id = ? " +
                "WHERE id = ?";

        if (jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId()
                ) == 0) {
            throw new EntityNotFoundException("Film not found");
        }

        String sqlDeleteGenresQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteGenresQuery, film.getId());

        String sqlUpdateGenresQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sqlUpdateGenresQuery, new FilmBatchPreparedStatementSetter(film));
        log.info("Film info updated");
        return film;
    }

    @Override
    public void deleteFilm(int filmId) {
        String sqlQuery = "DELETE FROM films WHERE id = ?";
        if (jdbcTemplate.update(sqlQuery, filmId) == 0) {
            throw new EntityNotFoundException("Film not found");
        }
        log.info("Film deleted");
    }

    @Override
    public List<Film> getPopular(int count) {
        if (count == 1) {
            log.info("The most popular film requested");
        } else {
            log.info(count + " most popular films requested");
        }
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa_rating_id," +
                " mpa.name AS mpa, GROUP_CONCAT(fg.genre_id) AS genre_id, GROUP_CONCAT(g.name) AS genre" +
                " FROM films AS f" +
                " JOIN mpa_ratings AS mpa ON f.mpa_rating_id = mpa.id" +
                " LEFT JOIN film_genres AS fg ON f.id = fg.film_id" +
                " LEFT JOIN genres AS g ON fg.genre_id = g.id" +
                " GROUP BY f.id" +
                " ORDER BY f.rate DESC" +
                " LIMIT ?";

        return jdbcTemplate.query(sqlQuery, filmRowMapper(), count);
    }

    @Override
    public void addLike(int filmId, long userId) {
        String sqlQuery = "INSERT INTO film_likes VALUES (?, ?);" +
                " UPDATE films SET rate = rate + 1 WHERE id = ?";

        try {
            jdbcTemplate.update(sqlQuery, filmId, userId, filmId);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("CONSTRAINT_7A:")) {
                throw new EntityNotFoundException("Film not found");
            } else if (e.getMessage().contains("CONSTRAINT_7A1:")) {
                throw new EntityNotFoundException("User not found");
            } else if (e.getMessage().contains("Unique index or primary key violation")) {
                throw new IllegalArgumentException("User already likes this Film");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        log.info("Like added");
    }

    @Override
    public void deleteLike(int filmId, long userId) {
        String sqlQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?;" +
                "UPDATE films SET rate = rate - 1 WHERE id = ?";

        if (jdbcTemplate.update(sqlQuery, filmId, userId, filmId) == 0) {
            throw new EntityNotFoundException("Film or User not found");
        }
        log.info("Like deleted");
    }

    private RowMapper<Film> filmRowMapper() {
        return (rs, rowNum) -> {
            Film film = new Film(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDate("release_date").toLocalDate(),
                    rs.getInt("duration"),
                    rs.getInt("rate"),
                    new MpaRating(rs.getShort("mpa_rating_id"), rs.getString("mpa"))
            );
            film.setId(rs.getInt("id"));
            if (rs.getString("genre_id") != null) {
                String[] genreIdsArray = rs.getString("genre_id").split(",");
                String[] genreNamesArray = rs.getString("genre").split(",");
                for (int i = 0; i < genreIdsArray.length; i++) {
                    short genreId = (short) Integer.parseInt(genreIdsArray[i]);
                    String genreName = genreNamesArray[i];
                    film.getGenres().add(new Genre(genreId, genreName));
                }
            }
            return film;
        };
    }

    static class FilmBatchPreparedStatementSetter implements BatchPreparedStatementSetter {
        private final Film film;

        FilmBatchPreparedStatementSetter(Film film) {
            this.film = film;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            List<Genre> genres = new ArrayList<>(film.getGenres());
            ps.setInt(1, film.getId());
            ps.setShort(2, genres.get(i).getId());
        }

        @Override
        public int getBatchSize() {
            return film.getGenres().size();
        }
    }

    private Map<String, Object> filmToMap(Film film) {
        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("name", film.getName());
        filmMap.put("description", film.getDescription());
        filmMap.put("release_date", Date.valueOf(film.getReleaseDate()));
        filmMap.put("duration", film.getDuration());
        filmMap.put("rate", film.getRate());
        filmMap.put("mpa_rating_id", film.getMpa().getId());

        return filmMap;
    }
}
