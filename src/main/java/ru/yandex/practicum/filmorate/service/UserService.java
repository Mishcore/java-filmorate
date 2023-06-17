package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(long followerId, long followedId) {
        User follower = userStorage.getUser(followerId);
        User followed = userStorage.getUser(followedId);

        follower.getFriends().add(followedId);
        log.info("User " + follower.getName() + " wants to be friends with User" + followed.getName());
        followed.getFriends().add(followerId);
        log.info("User " + follower.getName() + " and User " + followed.getName() + " become friends!");
    }

    public void deleteFriend(long followerId, long followedId) {
        User follower = userStorage.getUser(followerId);
        User followed = userStorage.getUser(followedId);

        follower.getFriends().remove(followedId);
        followed.getFriends().remove(followerId);
        log.info("User " + follower.getName() + " and User " + followed.getName() + " are no longer friends");
    }

    public List<User> getCommonFriends(long user1Id, long user2Id) {
        User user1 = userStorage.getUser(user1Id);
        User user2 = userStorage.getUser(user2Id);

        log.info("Common friends of users " + user1.getName() + " and " + user2.getName() + " requested");
        return user1.getFriends().stream()
                .filter(friendId -> user1.getFriends().contains(friendId) && user2.getFriends().contains(friendId))
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    public List<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }
}
