package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;
import ru.yandex.practicum.filmorate.model.film.MPA;
import ru.yandex.practicum.filmorate.model.film.FilmDTO;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.film.*;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@AllArgsConstructor
@Service
public class FilmService {

    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private final DirectorDbStorage directorStorage;
    private final UserService userService;

    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    public void addFilm(FilmDTO filmDTO) {
        checkDirectorId(filmDTO);
        storage.add(filmDTO);
    }

    public void putFilm(FilmDTO filmDTO) {
        checkDirectorId(filmDTO);
        storage.put(filmDTO);
    }

    public Film getFilm(int id) {
        return storage.getFilmById(id);
    }

    public String deleteFilm(int id) {
        storage.deleteFilmById(id);
        return "Film id: " + id + " deleted";
    }

    public String putLike(int filmId, int userId) {
        checkIds(filmId, userId);
        storage.putLike(filmId, userId);
        return String.format("User id: %s put like to film id: %s", userId, filmId);
    }

    public String deleteLike(int filmId, int userId) {
        checkIds(filmId, userId);
        storage.deleteLike(filmId, userId);
        return String.format("User id: %s deleted like from film id: %s", userId, filmId);
    }

    public Collection<Film> getDirectorFilms(int directorId, String sort) {
        if (!directorStorage.isContains(directorId)) {
            throw new ModelNotFoundException(String.format("Director id: %s not found", directorId));
        }
        if (!sort.equals("like") && !sort.equals("year")) {
            throw new UnsupportedOperationException(String.format("Sort by %s is not supported", sort));
        }
        return storage.getDirectorFilms(directorId, sort);
    }

    public Collection<Film> getPopularFilms(int count, int genreId, int year) {
        return storage.getPopularFilms(count, genreId, year);

    }

    public MPA getMpaByFilmId(int filmId) {
        return mpaStorage.getMpaById(filmId);
    }

    public Collection<MPA> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Genre getGenreById(int filmId) {
        return genreStorage.getGenreById(filmId);
    }

    public Collection<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Collection<Film> getFilmsBySearch(String query, String by) {
        if (query == null && by == null) {
            return storage.getSortedFilms();
        } else if (query == null || query.isEmpty()) {
            throw new UnsupportedOperationException("Query must be specified");
        } else if (by == null || by.isEmpty()) {
            throw new UnsupportedOperationException("Parameters must be specified");
        } else {
            String[] params = handleParamBy(by);
            if (params.length == 1) {
                return storage.getFilmsBySearch(query, params[0]);
            } else {
                List<Film> films = new ArrayList<>();
                for (String param : params) {
                    films.addAll(storage.getFilmsBySearch(query, param));
                }
                return films;
            }
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
        if (!storage.isContains(filmId)) {
            throw new ModelNotFoundException(String.format("Film id: %s not found", filmId));
        }
        if (!userStorage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
    }

    public Collection<Film> getCommonFilms(int userId, int friendId) {
        userService.checkIds(userId, friendId);
        return storage.getCommonFilms(userId, friendId);
    }

    private void checkDirectorId(FilmDTO filmDTO) {
        int directorId = filmDTO.getDirector().get(0).getId();
        if (!directorStorage.isContains(directorId)) {
            throw new ModelNotFoundException(String.format("Director id: %s not found", directorId));
        }
    }

}