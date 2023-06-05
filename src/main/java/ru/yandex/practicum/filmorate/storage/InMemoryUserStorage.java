package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        validateUserId(id);
        return users.get(id);
    }

    @Override
    public List<User> getFriends(int id) {
        return users.get(id).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
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
        validateUserId(user.getId());
        setEmptyNameAsLogin(user);
        users.replace(user.getId(), user);
        log.info("User updated");
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        validateUserId(userId);
        users.remove(userId);
        log.info("User deleted");
    }

    private void setEmptyNameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            log.warn("Invalid user ID");
            throw new EntityNotFoundException("Invalid user ID");
        }
        if (!users.containsKey(userId)) {
            log.warn("User not found");
            throw new EntityNotFoundException("User not found");
        }
    }
}
