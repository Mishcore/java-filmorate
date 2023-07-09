package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<MpaRating> getAllRatings() {
        log.info("MPA Ratings list requested");
        return mpaStorage.getAllRatings();
    }

    public MpaRating getRating(short id) {
        log.info("MPA Rating requested");
        return mpaStorage.getRating(id);
    }
}
