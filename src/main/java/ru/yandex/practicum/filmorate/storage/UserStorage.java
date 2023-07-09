package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAllUsers();

    User getUser(long id);

    List<User> getFriends(long id);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(long userId);

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getCommonFriends(long user1Id, long user2Id);
}
