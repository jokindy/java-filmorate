package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@Validated
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("Get films");
        return filmService.getFilms();
    }

    @PostMapping("/films")
    public String add(@Valid @RequestBody Film film) {
        log.info("Add film");
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public String update(@Valid @RequestBody Film film) {
        log.info("Put film");
        return filmService.putFilm(film);
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable @Positive(message = "Film id must be positive") int filmId) {
        log.info("Get user by id: {}", filmId);
        return filmService.getFilm(filmId);
    }

    @DeleteMapping("/films/{filmId}")
    public String deleteFilm(@PathVariable @Positive(message = "Film id must be positive") int filmId) {
        log.info("Delete film by id: {}", filmId);
        return filmService.deleteFilm(filmId);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public String putLikeToFilm(@PathVariable @Positive(message = "Film id must be positive") int filmId,
                                @PathVariable @Positive(message = "User id must be positive") int userId) {
        log.info(String.format("Put like to film id: %s from user id: %s", filmId, userId));
        return filmService.putLike(filmId, userId);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public String deleteLike(@PathVariable @Positive(message = "Film id must be positive") int filmId,
                             @PathVariable @Positive(message = "User id must be positive") int userId) {
        log.info(String.format("Delete film id: %s's like from from user id: %s", filmId, userId));
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10")
                                            @Positive(message = "Count must be positive") int count) {
        log.info("Get {} popular films", count);
        return filmService.getPopularFilms(count);
    }
}