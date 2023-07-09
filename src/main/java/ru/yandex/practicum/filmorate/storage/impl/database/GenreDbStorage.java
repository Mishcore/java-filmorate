package ru.yandex.practicum.filmorate.storage.impl.database;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getAllGenres() {
        String sqlQuery = "SELECT * FROM genres ORDER BY id";

        return jdbcTemplate.query(sqlQuery, genreRowMapper());
    }

    public Genre getGenre(short id) {
        if (id <= 0) {
            throw new EntityNotFoundException("Invalid genre ID");
        }

        String sqlQuery = "SELECT * FROM genres WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, genreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Genre not found");
        }
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre(
                rs.getByte("id"),
                rs.getString("name")
        );
    }
}
