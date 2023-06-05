package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private static final String CINEMA_BIRTH_DATE = "28.12.1895";

    private int id;

    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200)
    private String description;

    @ValidReleaseDate(earliestDate = CINEMA_BIRTH_DATE)
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private final Set<Integer> likers;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likers = new HashSet<>();
    }
}
