package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private static final String LOGIN_PATTERN = "^\\S+$";

    private long id;

    @NotBlank
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = LOGIN_PATTERN)
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

    private final Set<Long> friends;

    private final Set<Integer> likedFilms;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashSet<>();
        this.likedFilms = new HashSet<>();
    }
}
