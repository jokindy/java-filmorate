package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDTO;

import java.util.Collection;

public interface FilmStorage {

    void add(FilmDTO filmDTO);

    void put(FilmDTO filmDTO);

    Collection<Film> getFilms();

    Film getFilmById(int id);

    void deleteFilmById(int id);

    void putLike(int id, int userId);

    void deleteLike(int id, int userId);

    Collection<Film> getPopularFilms(int count);

    Collection<Film> getDirectorFilms(int directorId, String sort);

    boolean isContains(int id);
}
