package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();
    User getUser(int id);
    List<User> getFriends(int id);
    User addUser(User user);
    User updateUser(User user);
    void deleteUser(int userId);
}
