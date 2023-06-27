package ru.yandex.practicum.filmorate.storage.impl.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Repository
@Primary
@Slf4j
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<MpaRating> getAllRatings() {
        String sqlQuery = "SELECT * FROM mpa_ratings ORDER BY id";

        return jdbcTemplate.query(sqlQuery, mpaRowMapper());
    }

    public MpaRating getRating(short id) {
        if (id <= 0) {
            throw new EntityNotFoundException("Invalid MPA Rating ID");
        }

        String sqlQuery = "SELECT * FROM mpa_ratings WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, mpaRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("MPA Rating not found");
        }
    }

    private RowMapper<MpaRating> mpaRowMapper() {
        return (rs, rowNum) -> new MpaRating(
                rs.getByte("id"),
                rs.getString("name")
        );
    }
}