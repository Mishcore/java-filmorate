package ru.yandex.practicum.filmorate.storage.impl.database;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.model.Film;

import lombok.RequiredArgsConstructor;
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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import org.hamcrest.MatcherAssert;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmDbStorageTests {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage storage;
    private final GenreDbStorage genreStorage;

    @BeforeEach
    public void setup() {
        String filmsTestsSqlQuery = "INSERT INTO films " +
                "(name, description, release_date, duration, rate, mpa_rating_id) " +
                "VALUES ('title1', 'description1', '2000-01-01', '100', 4, 1)," +
                "('title2', 'description2', '2000-02-02', '110', 8, 2)," +
                "('title3', 'description3', '2000-03-03', '90', 10, 3);";
        jdbcTemplate.update(filmsTestsSqlQuery);
        String filmGenresTestsSqlQuery = "INSERT INTO film_genres (film_id, genre_id) " +
                "VALUES (1, 1), (1, 2), (1, 6), (2, 3), (3, 2), (3, 4);";
        jdbcTemplate.update(filmGenresTestsSqlQuery);
    }

    @ParameterizedTest
    @CsvSource({"1, title1, description1, 2000-01-01, 100, 4, 1, G, 1, 2, 6",
            "2, title2, description2, 2000-02-02, 110, 8, 2, PG, 3, 3, 3",
            "3, title3, description3, 2000-03-03, 90, 10, 3, PG-13, 2, 4, 4"})
    public void testGetAllFilms(int id, String name, String description, Date releaseDate,
                                int duration, int rate, short mpaId, String mpaName,
                                short genreId1, short genreId2, short genreId3) {
        List<Film> films = storage.getAllFilms();
        Film film = films.get(id - 1);

        assertEquals(3, films.size());
        assertThat(film)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("releaseDate", releaseDate.toLocalDate())
                .hasFieldOrPropertyWithValue("duration", duration)
                .hasFieldOrPropertyWithValue("rate", rate)
                .hasFieldOrPropertyWithValue("mpa", new MpaRating(mpaId, mpaName));

        Set<Genre> expectedGenres = new HashSet<>(Arrays.asList(
                genreStorage.getGenre(genreId1),
                genreStorage.getGenre(genreId2),
                genreStorage.getGenre(genreId3)
                )
        );
        MatcherAssert.assertThat(film.getGenres(), new GenreMatcher(expectedGenres));
    }

    @ParameterizedTest
    @CsvSource({"1, title1, description1, 2000-01-01, 100, 4, 1, G, 1, 2, 6",
            "2, title2, description2, 2000-02-02, 110, 8, 2, PG, 3, 3, 3",
            "3, title3, description3, 2000-03-03, 90, 10, 3, PG-13, 2, 4, 4"})
    public void testGetFilm(int id, String name, String description, Date releaseDate,
                            int duration, int rate, short mpaId, String mpaName,
                            short genreId1, short genreId2, short genreId3) {

        Film film = storage.getFilm(id);

        assertThat(film)
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name)
                .hasFieldOrPropertyWithValue("description", description)
                .hasFieldOrPropertyWithValue("releaseDate", releaseDate.toLocalDate())
                .hasFieldOrPropertyWithValue("duration", duration)
                .hasFieldOrPropertyWithValue("rate", rate)
                .hasFieldOrPropertyWithValue("mpa", new MpaRating(mpaId, mpaName));

        Set<Genre> expectedGenres = new HashSet<>(Arrays.asList(
                genreStorage.getGenre(genreId1),
                genreStorage.getGenre(genreId2),
                genreStorage.getGenre(genreId3)
        )
        );
        MatcherAssert.assertThat(film.getGenres(), new GenreMatcher(expectedGenres));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 4, 999})
    public void shouldThrowEntityNotFoundExceptionWhenPassingWrongId(int id) {
        assertThrows(EntityNotFoundException.class, () -> storage.getFilm(id));
    }

    @Test
    public void testAddFilm() {
        Film film = new Film("newFilm", "newDescription", LocalDate.of(2000, 4, 4), 120, 2, new MpaRating((short) 1, "G"));
        film.setGenres(Set.of(new Genre((short) 5, "Документальный")));
        storage.addFilm(film);
        assertThat(storage.getFilm(film.getId()))
                .hasFieldOrPropertyWithValue("id", 4)
                .hasFieldOrPropertyWithValue("name", "newFilm")
                .hasFieldOrPropertyWithValue("description", "newDescription")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2000-04-04"))
                .hasFieldOrPropertyWithValue("duration", 120)
                .hasFieldOrPropertyWithValue("rate", 2)
                .hasFieldOrPropertyWithValue("mpa", new MpaRating((short) 1, "G"));

        Set<Genre> expectedGenres = new HashSet<>(Collections.singletonList(genreStorage.getGenre((short) 5)));
        MatcherAssert.assertThat(film.getGenres(), new GenreMatcher(expectedGenres));
    }

    @Test
    public void testUpdateFilm() {
        Film film = new Film("newFilm", "newDescription", LocalDate.of(2000, 4, 4), 120, 2, new MpaRating((short) 1, "G"));
        film.setGenres(Set.of(new Genre((short) 5, "Документальный")));
        film.setId(1);
        storage.updateFilm(film);
        assertEquals(3, storage.getAllFilms().size());
        assertThat(storage.getFilm(film.getId()))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "newFilm")
                .hasFieldOrPropertyWithValue("description", "newDescription")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.parse("2000-04-04"))
                .hasFieldOrPropertyWithValue("duration", 120)
                .hasFieldOrPropertyWithValue("rate", 2)
                .hasFieldOrPropertyWithValue("mpa", new MpaRating((short) 1, "G"));

        Set<Genre> expectedGenres = new HashSet<>(Collections.singletonList(genreStorage.getGenre((short) 5)));
        MatcherAssert.assertThat(film.getGenres(), new GenreMatcher(expectedGenres));
    }

    @Test
    public void testDeleteFilm() {
        storage.deleteFilm(1);
        assertEquals(2, storage.getAllFilms().size());
    }

    @Test
    public void testGetPopular() {
        List<Film> popularFilms = storage.getPopular(3);
        assertEquals(3, popularFilms.size());
        assertEquals(storage.getFilm(3), popularFilms.get(0));
        assertEquals(storage.getFilm(2), popularFilms.get(1));
        assertEquals(storage.getFilm(1), popularFilms.get(2));
    }

    @ParameterizedTest
    @CsvSource({"1, 1, 4", "2, 1, 8", "3, 1, 10"})
    public void testAddLikeDeleteLike(int filmId, long userId, int rate) {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.addUser(new User("test@mail.com", "updated_login", "updated_name", LocalDate.of(2001, 2, 5)));

        storage.addLike(filmId, userId);
        Film likedFilm = storage.getFilm(filmId);
        assertEquals(rate + 1, likedFilm.getRate());
        assertEquals(1, likedFilm.getLikers().size());

        storage.deleteLike(filmId, userId);
        Film unlikedFilm = storage.getFilm(filmId);
        assertEquals(rate, unlikedFilm.getRate());
        assertEquals(0, unlikedFilm.getLikers().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 4, 999})
    public void shouldThrowEntityNotFoundExceptionWhenAddingOrDeletingLikeToUnknownFilmOrFromUnknownUser(int wrongId) {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.addUser(new User("test@mail.com", "updated_login", "updated_name", LocalDate.of(2001, 2, 5)));

        assertThrows(EntityNotFoundException.class, () -> storage.addLike(wrongId, 1));
        assertThrows(EntityNotFoundException.class, () -> storage.addLike(1, wrongId));
        assertThrows(EntityNotFoundException.class, () -> storage.deleteLike(1, wrongId));
        assertThrows(EntityNotFoundException.class, () -> storage.deleteLike(wrongId, 1));
    }

    @Test
    public void shouldThrowExceptionWhenAddingLikeToFilmOrDeletingLikeTwice() {
        UserDbStorage userStorage = new UserDbStorage(jdbcTemplate);
        userStorage.addUser(new User("test@mail.com", "updated_login", "updated_name", LocalDate.of(2001, 2, 5)));

        storage.addLike(1, 1);
        assertThrows(IllegalArgumentException.class, () -> storage.addLike(1, 1));
        storage.deleteLike(1, 1);
        assertThrows(EntityNotFoundException.class, () -> storage.deleteLike(1, 1));
    }

    static class GenreMatcher extends TypeSafeMatcher<Set<Genre>> {
        private final Set<Genre> expectedGenres;

        public GenreMatcher(Set<Genre> expectedGenres) {
            this.expectedGenres = expectedGenres;
        }

        @Override
        protected boolean matchesSafely(Set<Genre> actualGenres) {
            if (actualGenres.size() != expectedGenres.size()) {
                return false;
            }
            for (Genre expectedGenre : expectedGenres) {
                boolean found = false;
                for (Genre actualGenre : actualGenres) {
                    if (expectedGenre.getId() == actualGenre.getId()
                            && expectedGenre.getName().equals(actualGenre.getName())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Genres ").appendValue(expectedGenres);
        }
    }
}
