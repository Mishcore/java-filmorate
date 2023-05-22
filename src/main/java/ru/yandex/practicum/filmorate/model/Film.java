package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {

    @PositiveOrZero
    private long id = 0;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @ValidReleaseDate
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
