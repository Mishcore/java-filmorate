package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.EntityValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        log.info("User list requested");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@Valid @RequestBody User user) {
        setEmptyNameAsLogin(user);
        user.setId(++id);
        users.put(id, user);
        log.info("User created");
        return user;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Invalid user (request body has no user id)");
            throw new EntityValidationException("Invalid user (request body has no user id)");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("User not found");
            throw new EntityNotFoundException(user);
        }
        setEmptyNameAsLogin(user);
        users.replace(user.getId(), user);
        log.info("User updated");
        return user;
    }

    private void setEmptyNameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
