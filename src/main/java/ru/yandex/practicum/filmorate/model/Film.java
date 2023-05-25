package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private static final String CINEMA_BIRTH_DATE = "28.12.1895";

    @Positive
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200)
    private String description;

    @ValidReleaseDate(earliestDate = CINEMA_BIRTH_DATE)
    private LocalDate releaseDate;

    @Positive
    private int duration;

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
