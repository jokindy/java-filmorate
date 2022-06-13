package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {

    private final FilmStorage storage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("FilmDbStorage") FilmStorage storage,
                       @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
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

    private void checkIds(int filmId, int userId) {
        if (!storage.isContains(filmId)) {
            throw new ModelNotFoundException(String.format("Film id: %s not found", filmId));
        }
        if (!userStorage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
    }
}