package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.FilmDTO;

import java.util.Collection;

public interface FilmStorage {

    void add(FilmDTO filmDTO);

    void put(FilmDTO filmDTO);

    Collection<Film> getFilms();

    Film getFilmById(int id);

    Collection<Film> getFilmsBySearch(String query, String by);

    Collection<Film> getSortedFilms();

    void deleteFilmById(int id);

    void putLike(int id, int userId);

    void deleteLike(int id, int userId);

    Collection<Film> getPopularFilms(int count, int genreId, int year);

    Collection<Film> getDirectorFilms(int directorId, String sort);

    boolean isContains(int id);

    Collection<Film> getCommonFilms(int userId, int friendId);

}
