package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.EntityValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private static int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        log.info("User list requested");
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        setEmptyNameAsLogin(user);
        user.setId(++id);
        users.put(id, user);
        log.info("User created");
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            log.warn("Invalid user (request body has no user id)");
            throw new EntityValidationException("Invalid user (request body has no user id)");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("User not found");
            throw new EntityNotFoundException(User.class.getSimpleName());
        }
        setEmptyNameAsLogin(user);
        users.replace(user.getId(), user);
        log.info("User updated");
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        if (!users.containsKey(userId)) {
            log.warn("User not found");
            throw new EntityNotFoundException(User.class.getSimpleName());
        }
        users.remove(userId);
        log.info("User deleted");
    }

    private void setEmptyNameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
