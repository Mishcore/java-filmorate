package ru.yandex.practicum.filmorate.storage.impl.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.filmorate.model.Genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class GenreDbStorageTests {
    private final GenreDbStorage storage;

    @ParameterizedTest
    @CsvSource({"1, Комедия", "2, Драма", "3, Мультфильм", "4, Триллер", "5, Документальный", "6, Боевик"})
    public void testGetAllGenres(short id, String name) {
        List<Genre> genreList = storage.getAllGenres();

        assertEquals(6, genreList.size());
        assertThat(genreList.get(id - 1))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name);
    }

    @ParameterizedTest
    @CsvSource({"1, Комедия", "2, Драма", "3, Мультфильм", "4, Триллер", "5, Документальный", "6, Боевик"})
    public void testGetGenre(short id, String name) {
        Genre genre = storage.getGenre(id);

        assertThat(genre)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name);
    }
}
