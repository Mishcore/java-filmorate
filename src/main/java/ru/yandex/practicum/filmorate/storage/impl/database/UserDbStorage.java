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
        log.info("Users list requested");
        return jdbcTemplate.query(sqlQuery, userRowMapper());
    }

    @Override
    public User getUser(long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        log.info("User requested");
        try {
            return jdbcTemplate.queryForObject(sqlQuery, userRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("User not found");
        }
    }

    @Override
    public List<User> getFriends(long id) {
        log.info("User friends list requested");
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
        log.info("User created");
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
        log.info("User updated");
        return user;
    }

    @Override
    public void deleteUser(long userId) {
        String sqlQuery = "DELETE FROM users WHERE id = ?";

        if (jdbcTemplate.update(sqlQuery, userId) == 0) {
            throw new EntityNotFoundException("User not found");
        }
        log.info("User deleted");
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
        log.info("Friend added");
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sqlQuery = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";

        if (jdbcTemplate.update(sqlQuery, userId, friendId) == 0) {
            throw new EntityNotFoundException("User(s) not found");
        }
        log.info("Friend deleted");
    }

    @Override
    public List<User> getCommonFriends(long user1Id, long user2Id) {
        String sqlQuery = "SELECT u.* FROM users u" +
                " JOIN user_friends uf1 ON u.id = uf1.friend_id JOIN user_friends uf2 ON uf1.friend_id = uf2.friend_id" +
                " WHERE uf1.user_id = ? AND uf2.user_id = ?";
        log.info("Common friends list requested");
        List<User> commonFriends = jdbcTemplate.query(sqlQuery, userRowMapper(), user1Id, user2Id);

        if (commonFriends.isEmpty()) {
            List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id IN (?, ?)",
                    userRowMapper(), user1Id, user2Id);
            if (users.size() < 2) {
                throw new EntityNotFoundException("User(s) not found");
            } else {
                return commonFriends;
            }
        }
        return commonFriends;
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
