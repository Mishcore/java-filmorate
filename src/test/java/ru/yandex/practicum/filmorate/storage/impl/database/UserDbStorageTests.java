package ru.yandex.practicum.filmorate.storage.impl.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserDbStorageTests {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage storage;

    @BeforeEach
    public void setup() {
        String userTestsSqlQuery = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES ('test@mail.ru', 'login1', 'name1', '2000-01-01')," +
                " ('test@gmail.com', 'login2', 'name2', '2000-01-02')," +
                " ('test@yandex.ru', 'login3', 'name3', '2000-01-03');";
        jdbcTemplate.update(userTestsSqlQuery);
        String userFriendsTestsSqlQuery = "INSERT INTO user_friends " +
                "VALUES (1, 2), (1, 3), (2, 1), (2, 3), (3, 2);";
        jdbcTemplate.update(userFriendsTestsSqlQuery);
    }

    @ParameterizedTest
    @CsvSource({"1, test@mail.ru, login1, name1, 2000-01-01",
            "2, test@gmail.com, login2, name2, 2000-01-02",
            "3, test@yandex.ru, login3, name3, 2000-01-03"})
    public void testGetAllUsers(long id, String email, String login, String name, Date birthday) {
        List<User> users = storage.getAllUsers();

        assertEquals(3, users.size());
        assertThat(users.get((int) id - 1))
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("login", login)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("birthday", birthday.toLocalDate());
    }

    @ParameterizedTest
    @CsvSource({"1, test@mail.ru, login1, name1, 2000-01-01",
            "2, test@gmail.com, login2, name2, 2000-01-02",
            "3, test@yandex.ru, login3, name3, 2000-01-03"})
    public void testGetUser(long id, String email, String login, String name, Date birthday) {
        assertThat(storage.getUser(id))
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("email", email)
                .hasFieldOrPropertyWithValue("login", login)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("birthday", birthday.toLocalDate());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 4, 999})
    public void shouldThrowEntityNotFoundExceptionWhenPassingWrongId(long id) {
        assertThrows(EntityNotFoundException.class, () -> storage.getUser(id));
    }

    @ParameterizedTest
    @CsvSource({"1, 2, 3", "2, 1, 3", "3, 2, 2"})
    public void testGetFriends(long id, long friendOneId, long friendTwoId) {
        Set<Long> friendsIdSet = new HashSet<>();
        storage.getFriends(id).stream()
                .map(User::getId)
                .forEach(friendsIdSet::add);
        assertTrue(friendsIdSet.contains(friendOneId));
        assertTrue(friendsIdSet.contains(friendTwoId));
    }

    @Test
    public void testAddUser() {
        User user = storage.addUser(new User("test@new.ru", "new_login", "new_name", LocalDate.of(2000, 1, 4)));
        assertThat(storage.getUser(user.getId()))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 4L)
                .hasFieldOrPropertyWithValue("email", "test@new.ru")
                .hasFieldOrPropertyWithValue("login", "new_login")
                .hasFieldOrPropertyWithValue("name", "new_name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("2000-01-04"));
    }

    @Test
    public void testUpdateUser() {
        User newUser = new User("test@mail.com", "updated_login", "updated_name", LocalDate.of(2001, 2, 5));
        newUser.setId(1);

        storage.updateUser(newUser);
        assertEquals(3, storage.getAllUsers().size());
        assertThat(storage.getUser(newUser.getId()))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("email", "test@mail.com")
                .hasFieldOrPropertyWithValue("login", "updated_login")
                .hasFieldOrPropertyWithValue("name", "updated_name")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.parse("2001-02-05"));
    }

    @Test
    public void testDeleteUser() {
        storage.deleteUser(1L);
        assertEquals(2, storage.getAllUsers().size());
    }

    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 4, 999})
    public void shouldThrowEntityNotFoundExceptionWhenNoUserToUpdateOrDelete(long id) {
        User newUser = new User("test@mail.com", "updated_login", "updated_name", LocalDate.of(2001, 2, 5));
        newUser.setId(id);
        assertThrows(EntityNotFoundException.class, () -> storage.updateUser(newUser));
        assertThrows(EntityNotFoundException.class, () -> storage.deleteUser(id));
    }

    @ParameterizedTest
    @CsvSource({"1, 4, 3", "2, 4, 3", "3, 1, 2"})
    public void testAddFriend(long userid, long friendId, int size) {
        storage.addUser(new User("test@new.ru", "new_login", "new_name", LocalDate.of(2000, 1, 4)));
        assertEquals(size - 1, storage.getFriends(userid).size());
        storage.addFriend(userid, friendId);
        assertEquals(size, storage.getFriends(userid).size());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenAddingFriendAlreadyAdded() {
        storage.addFriend(3, 1);
        assertEquals(2, storage.getFriends(3).size());
        assertThrows(IllegalArgumentException.class, () -> storage.addFriend(3, 1));
        assertEquals(2, storage.getFriends(3).size());
    }

    @ParameterizedTest
    @CsvSource({"1, 3, 1", "2, 1, 1", "3, 2, 0"})
    public void testDeleteFriend(long userid, long friendId, int size) {
        assertEquals(size + 1, storage.getFriends(userid).size());
        storage.deleteFriend(userid, friendId);
        assertEquals(size, storage.getFriends(userid).size());
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenDeletingFriendAlreadyDeletedOrNeverAdded() {
        storage.deleteFriend(3, 2);
        assertThrows(EntityNotFoundException.class, () -> storage.deleteFriend(3, 2));
        assertThrows(EntityNotFoundException.class, () -> storage.deleteFriend(3, 1));
    }

    @ParameterizedTest
    @CsvSource({"1, 2, 1", "1, 3, 1", "3, 2, 0"})
    public void testGetCommonFriends(long userid, long friendId, int size) {
        assertEquals(size, storage.getCommonFriends(userid, friendId).size());
    }

    @ParameterizedTest
    @CsvSource({"1, 4", "4, 2", "4, 5", "69, 777"})
    public void shouldThrowEntityNotFoundExceptionWhenGettingCommonFriendsOfUnknownUsers(long userid, long friendId) {
        assertThrows(EntityNotFoundException.class, () -> storage.getCommonFriends(userid, friendId));
    }

}
