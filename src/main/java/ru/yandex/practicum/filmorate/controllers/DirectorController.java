package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@Validated
public class DirectorController {

    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping("/directors")
    public Collection<Director> findAll() {
        log.info("Get director");
        return directorService.getDirectors();
    }

    @PostMapping("/directors")
    public Director add(@Valid @RequestBody Director director) {
        log.info("Add director");
        directorService.addDirector(director);
        return director;
    }

    @PutMapping("/directors")
    public Director update(@Valid @RequestBody Director director) {
        log.info("Put director");
        directorService.putDirector(director);
        return director;
    }

    @GetMapping("/directors/{directorId}")
    public Director getFilm(@PathVariable int directorId) {
        log.info("Get director by id: {}", directorId);
        return directorService.getDirector(directorId);
    }

    @DeleteMapping("/directors/{directorId}")
    public String deleteDirector(@PathVariable int directorId) {
        log.info("Delete director by id: {}", directorId);
        return directorService.deleteDirector(directorId);
    }
}