package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.*;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {

    private final FilmStorage storage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreStorage;
    private final MpaDbStorage mpaStorage;
    private final DirectorDbStorage directorStorage;

    @Autowired
    public FilmService(FilmStorage storage, UserStorage userStorage, GenreDbStorage genreDbStorage,
                       MpaDbStorage mpaDbStorage, DirectorDbStorage directorDbStorage) {
        this.storage = storage;
        this.userStorage = userStorage;
        this.genreStorage = genreDbStorage;
        this.mpaStorage = mpaDbStorage;
        this.directorStorage = directorDbStorage;
    }

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


    private void checkIds(int filmId, int userId) {
        if (!storage.isContains(filmId)) {
            throw new ModelNotFoundException(String.format("Film id: %s not found", filmId));
        }
        if (!userStorage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
    }

    private void checkDirectorId(FilmDTO filmDTO) {
        int directorId = filmDTO.getDirector().get(0).getId();
        if (!directorStorage.isContains(directorId)) {
            throw new ModelNotFoundException(String.format("Director id: %s not found", directorId));
        }
    }
}