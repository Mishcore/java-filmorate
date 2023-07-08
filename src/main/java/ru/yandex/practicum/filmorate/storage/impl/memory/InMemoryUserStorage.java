package ru.yandex.practicum.filmorate.storage.impl.memory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private long id = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        log.info("Users list requested");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(long id) {
        validateUserId(id);
        log.info("User requested");
        return users.get(id);
    }

    @Override
    public List<User> getFriends(long id) {
        validateUserId(id);
        log.info("User friends list requested");
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
    public void deleteUser(long userId) {
        validateUserId(userId);
        users.remove(userId);
        log.info("User deleted");
    }

    @Override
    public void addFriend(long userId, long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        users.get(userId).getFriends().add(friendId);
        users.get(friendId).getFriends().add(userId);
        log.info("Friend added");
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        users.get(userId).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(userId);
        log.info("Friend deleted");
    }

    @Override
    public List<User> getCommonFriends(long user1Id, long user2Id) {
        validateUserId(user1Id);
        validateUserId(user2Id);
        log.info("Common friends list requested");
        return users.get(user1Id).getFriends().stream()
                .filter((user) -> users.get(user2Id).getFriends().contains(user))
                .map(users::get)
                .collect(Collectors.toList());
    }

    private void setEmptyNameAsLogin(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUserId(long userId) {
        if (userId <= 0) {
            throw new EntityNotFoundException("Invalid user ID");
        }
        if (!users.containsKey(userId)) {
            throw new EntityNotFoundException("User not found");
        }
    }
}
