package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Film;
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
    public Film add(@Valid @RequestBody Film film) {
        log.info("Add film");
        filmService.addFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Put film");
        filmService.putFilm(film);
        return film;
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        log.info("Get user by id: {}", filmId);
        return filmService.getFilm(filmId);
    }

    @DeleteMapping("/films/{filmId}")
    public String deleteFilm(@PathVariable int filmId) {
        log.info("Delete film by id: {}", filmId);
        return filmService.deleteFilm(filmId);
    }

    @PutMapping("/films/{filmId}/like/{userId}")
    public String putRateToFilm(@PathVariable int filmId, @PathVariable int userId, @RequestParam int rate) {
        log.info("Put like to film id: {} from user id: {}", filmId, userId);
        return filmService.putRate(filmId, userId, rate);
    }

    @DeleteMapping("/films/{filmId}/like/{userId}")
    public String deleteLike(@PathVariable int filmId, @PathVariable int userId) {
        log.info("Delete film id: {}'s like from from user id: {}", filmId, userId);
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10")
                                            @Positive(message = "Count must be positive") int count,
                                            @RequestParam(required = false, defaultValue = "0") int genreId,
                                            @RequestParam(required = false, defaultValue = "0") int year) {
        log.info("Get {} popular films", count);
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/search")
    public Collection<Film> getFoundFilms(@RequestParam(required = false) String query,
                                          @RequestParam(required = false) String by) {
        log.info("Get films by searching");
        return filmService.getFilmsBySearch(query, by);
    }

    @GetMapping("/films/director/{directorId}")
    public Collection<Film> getDirectorFilms(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("Get films by director id: {} by {}", directorId, sortBy);
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/films/common")
    public Collection<Film> commonFilms(@RequestParam int userId, @RequestParam int friendId) {
        log.info("Found shared movies for users with id: {}, {}", userId, friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}