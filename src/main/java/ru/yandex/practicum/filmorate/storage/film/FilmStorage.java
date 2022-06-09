package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

public interface FilmStorage {

    void add(Film film);

    void put(Film film);

    Collection<Film> getFilms();

    Collection<MPA> getAllMpa();

    Collection<Genre> getAllGenres();

    Film getFilmById(int id);

    MPA getMpaById(int id);

    Genre getGenreById(int id);

    Collection<Film> getFilmsBySearch(String query, String by);

    Collection<Film> getSortedFilms();

    void deleteFilmById(int id);

    void putLike(int id, int userId);

    void deleteLike(int id, int userId);

    Collection<Film> getPopularFilms(int count);

    boolean isContains(int id);
}
