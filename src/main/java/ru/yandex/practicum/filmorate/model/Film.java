package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.ValidReleaseDate;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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

    @PositiveOrZero
    private int rate;

    private MpaRating mpa;

    private final Set<Genre> genres;

    private final Set<Long> likers;

    public Film(String name, String description, LocalDate releaseDate, int duration, int rate, MpaRating mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        this.mpa = mpa;
        this.genres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        this.likers = new HashSet<>();
    }

    @JsonSetter
    public void setGenres(Set<Genre> genres) {
        this.genres.clear();
        this.genres.addAll(genres);
    }

    public void addLike(long userId) {
        likers.add(userId);
        rate++;
    }

    public void deleteLike(long userId) {
        likers.remove(userId);
        rate--;
    }
}
