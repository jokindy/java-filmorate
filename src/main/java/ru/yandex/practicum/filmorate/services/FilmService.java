package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.film.*;
import ru.yandex.practicum.filmorate.storage.film.*;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorDbStorage directorStorage;
    private final UserService userService;

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void addFilm(Film film) {
        checkDirectorId(film);
        filmStorage.add(film);
    }

    public void putFilm(Film film) {
        checkDirectorId(film);
        filmStorage.put(film);
    }

    public Film getFilm(int id) {
        return filmStorage.getFilmById(id);
    }

    public String deleteFilm(int id) {
        filmStorage.deleteFilmById(id);
        return "Film id: " + id + " deleted";
    }

    public String putRate(int filmId, int userId, int rate) {
        checkIds(filmId, userId);
        if (rate < 1 || rate > 10) throw new ValidationException("Not valid rate");
        filmStorage.putRate(filmId, userId, rate);
        return String.format("User id: %s put rate %s to film id: %s", userId, rate, filmId);
    }

    public String deleteLike(int filmId, int userId) {
        checkIds(filmId, userId);
        filmStorage.deleteRate(filmId, userId);
        return String.format("User id: %s deleted like from film id: %s", userId, filmId);
    }

    public Collection<Film> getDirectorFilms(int directorId, String sort) {
        if (!directorStorage.isContains(directorId)) {
            throw new ModelNotFoundException(String.format("Director id: %s not found", directorId));
        }
        if (!sort.equals("like") && !sort.equals("year")) {
            throw new UnsupportedOperationException(String.format("Sort by %s is not supported", sort));
        }
        return filmStorage.getDirectorFilms(directorId, sort);
    }

    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public Collection<Film> getFilmsBySearch(String query, String by) {
        if (query == null && by == null) {
            log.info("Query and parameters not specified. Returning sorted films");
            return filmStorage.getSortedFilms();
        } else if (query == null || query.isEmpty()) {
            throw new UnsupportedOperationException("Query must be specified");
        } else if (by == null || by.isEmpty()) {
            throw new UnsupportedOperationException("Parameters must be specified");
        } else {
            String[] params = handleParamBy(by);
            List<Film> films = new ArrayList<>();
            for (String param : params) {
                films.addAll(filmStorage.getFilmsBySearch(query, param));
                log.info("Searching films where query: {} in {}", query, param);
            }
            return films;
        }
    }

    private String[] handleParamBy(String by) {
        String[] params = by.split(",");
        if (params.length > 2) {
            throw new UnsupportedOperationException("Too much parameters for query");
        }
        for (String param : params) {
            if (!param.equals("title") && !param.equals("director")) {
                throw new UnsupportedOperationException("Unsupported parameter - " + params[0]);
            }
        }
        return params;
    }

    private void checkIds(int filmId, int userId) {
        if (!filmStorage.isContains(filmId)) {
            throw new ModelNotFoundException(String.format("Film id: %s not found", filmId));
        }
        if (!userStorage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        userService.checkIds(userId, friendId);
        return filmStorage.getCommonFilms(userId, friendId);
    }

    private void checkDirectorId(Film film) {
        Integer directorId = film.getDirector().getId();
        if (directorId != null) {
            if (!directorStorage.isContains(directorId)) {
                throw new ModelNotFoundException(String.format("Director id: %s not found", directorId));
            }
        }
    }
}