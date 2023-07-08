package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public List<User> getAllUsers() {
        log.info("Users list requested");
        return userStorage.getAllUsers();
    }

    public User getUser(long id) {
        if (id <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        log.info("User requested");
        return userStorage.getUser(id);
    }

    public List<User> getFriends(long id) {
        if (id <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        log.info("User friends list requested");
        return userStorage.getFriends(id);
    }

    public User addUser(User user) {
        setEmptyNameAsLogin(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        setEmptyNameAsLogin(user);
        return userStorage.updateUser(user);
    }

    public void deleteUser(long id) {
        if (id <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        userStorage.deleteUser(id);
    }

    public void addFriend(long userId, long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        if (userId == friendId) {
            throw new IllegalArgumentException("Users cannot add themselves to friends");
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        if (userId <= 0 || friendId <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        if (userId == friendId) {
            throw new IllegalArgumentException("Users cannot add themselves to friends");
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getCommonFriends(long user1Id, long user2Id) {
        if (user1Id <= 0 || user2Id <= 0) {
            throw new EntityNotFoundException("Invalid User ID");
        }
        if (user1Id == user2Id) {
            throw new IllegalArgumentException("Cannot pass a pair of same ids" +
                    " (Might want to call getFriends(long id) method instead)");
        }
        log.info("Common friends list requested");
        return userStorage.getCommonFriends(user1Id, user2Id);
    }

    private void setEmptyNameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
