package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private static final String LOGIN_PATTERN = "^\\S+$";

    @Positive
    private Integer id;

    @NotNull
    @Email
    private String email;

    @NotNull
    @Pattern(regexp = LOGIN_PATTERN)
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        if (name == null || name.isEmpty()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = birthday;
    }
}
