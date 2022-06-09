package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {

    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage, GenreDbStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.genreStorage = genreDbStorage;
        this.mpaStorage = mpaDbStorage;
    }

    public Collection<Film> getFilms() {
        return storage.getFilms();
    }

    public void addFilm(Film film) {
        storage.add(film);
    }

    public void putFilm(Film film) {
        storage.put(film);
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

    public Collection<Film> getPopularFilms(int count) {
        return storage.getPopularFilms(count);
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
        } else if (query == null) {
            throw new ModelNotFoundException("You must write query");
        } else if (by == null) {
            throw new ModelNotFoundException("You must write by");
        } else {
            return storage.getFilmsBySearch(query, by);
        }
    }

    private String[] handleParamBy(String by) {

    }

    private void checkIds(int filmId, int userId) {
        if (!storage.isContains(filmId)) {
            throw new ModelNotFoundException(String.format("Film id: %s not found", filmId));
        }
        if (!userStorage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
    }
}