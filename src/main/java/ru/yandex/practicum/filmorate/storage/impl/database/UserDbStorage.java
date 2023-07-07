package ru.yandex.practicum.filmorate.storage.impl.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Primary
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users GROUP BY id";

        return jdbcTemplate.query(sqlQuery, userRowMapper());
    }

    @Override
    public User getUser(long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sqlQuery, userRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    public List<User> getFriends(long id) {
        String sqlQuery = "SELECT u.id, u.email, u.login, u.name, u.birthday" +
                " FROM user_friends AS uf" +
                " JOIN users AS u ON u.id = uf.friend_id" +
                " WHERE user_id = ?";

        try {
            return jdbcTemplate.query(sqlQuery, userRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(userToMap(user)).longValue());

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";

        if (jdbcTemplate.update(
                sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        ) == 0) {
            throw new EntityNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public void deleteUser(long userId) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";

        if (jdbcTemplate.update(sqlQuery, userId) == 0) {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO user_friends VALUES (?, ?)";

        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("CONSTRAINT_BD:")) {
                throw new EntityNotFoundException("User not found");
            } else if (e.getMessage().contains("CONSTRAINT_BD6:")) {
                throw new EntityNotFoundException("User friend not found");
            } else if (e.getMessage().contains("Unique index or primary key violation")) {
                throw new IllegalArgumentException("Users are already friends");
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";

        if (jdbcTemplate.update(sqlQuery, userId, friendId) == 0) {
            throw new EntityNotFoundException("User(s) not found");
        }
    }

    @Override
    public List<User> getCommonFriends(long user1Id, long user2Id) {
        String sqlQuery = "SELECT * FROM users WHERE id IN" +
                " (SELECT friend_id FROM user_friends WHERE user_id = ? AND friend_id IN" +
                " (SELECT friend_id FROM user_friends WHERE user_id = ?))";

        return jdbcTemplate.query(sqlQuery, userRowMapper(), user1Id, user2Id);
    }

    private RowMapper<User> userRowMapper() {
        return (rs, rowNum) -> {
            User user = new User(
                    rs.getString("email"),
                    rs.getString("login"),
                    rs.getString("name"),
                    rs.getDate("birthday").toLocalDate()
            );
            user.setId(rs.getLong("id"));

            return user;
        };
    }

    private Map<String, Object> userToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", user.getEmail());
        userMap.put("login", user.getLogin());
        userMap.put("name", user.getName());
        userMap.put("birthday", Date.valueOf(user.getBirthday()));

        return userMap;
    }
}
