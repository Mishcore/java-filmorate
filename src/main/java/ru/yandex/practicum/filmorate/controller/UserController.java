package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static long id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        setEmptyNameAsLogin(user);
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User is not found");
        }
        setEmptyNameAsLogin(user);
        users.replace(user.getId(), user);
        return user;
    }

    private void setEmptyNameAsLogin(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }
}
