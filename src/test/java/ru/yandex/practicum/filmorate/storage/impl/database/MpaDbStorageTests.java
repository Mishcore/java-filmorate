package ru.yandex.practicum.filmorate.storage.impl.database;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.yandex.practicum.filmorate.model.MpaRating;

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
public class MpaDbStorageTests {
    private final MpaDbStorage storage;

    @ParameterizedTest
    @CsvSource({"1, G", "2, PG", "3, PG-13", "4, R", "5, NC-17"})
    public void testGetAllRatings(short id, String name) {
        List<MpaRating> mpaList = storage.getAllRatings();

        assertEquals(5, mpaList.size());
        assertThat(mpaList.get(id - 1))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name);
    }

    @ParameterizedTest
    @CsvSource({"1, G", "2, PG", "3, PG-13", "4, R", "5, NC-17"})
    void testGetRating(short id, String name) {
        MpaRating mpa = storage.getRating(id);

        assertThat(mpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", id)
                .hasFieldOrPropertyWithValue("name", name);
    }
}