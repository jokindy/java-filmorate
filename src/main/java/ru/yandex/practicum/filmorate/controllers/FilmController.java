package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utilities.Handler;
import ru.yandex.practicum.filmorate.utilities.Storage;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
public class FilmController {

    private final Storage<Film> films = new Storage<>("Film");

    @GetMapping("/films")
    public Map<Integer, Film> findAll() {
        log.info("Get films");
        return films.getMap();
    }

    @PostMapping("/films")
    public String add(@Valid @RequestBody Film film) {
        return new Handler<Film>().handle(POST, films, film);
    }

    @PutMapping("/films")
    public String update(@Valid @RequestBody Film film) {
        return new Handler<Film>().handle(PUT, films, film);
    }
}
