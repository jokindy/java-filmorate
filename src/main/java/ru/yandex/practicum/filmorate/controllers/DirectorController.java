package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@Validated
public class DirectorController {

    private final FilmService directorService;

    public DirectorController(FilmService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("Get director");
        return directorService.getFilms();
    }

    @PostMapping("/films")
    public Film add(@Valid @RequestBody Film film) {
        log.info("Add film");
        directorService.addFilm(film);
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("Put film");
        directorService.putFilm(film);
        return film;
    }

    @GetMapping("/films/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        log.info("Get user by id: {}", filmId);
        return directorService.getFilm(filmId);
    }

    @DeleteMapping("/films/{filmId}")
    public String deleteFilm(@PathVariable int filmId) {
        log.info("Delete film by id: {}", filmId);
        return directorService.deleteFilm(filmId);
    }
}
