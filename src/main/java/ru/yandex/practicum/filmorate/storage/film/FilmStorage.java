package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    void add(Film film);

    void put(Film film);

    Collection<Film> getFilms();

    Film getFilmById(int id);

    void deleteFilmById(int id);

    void putLike(int id, int userId);

    void deleteLike(int id, int userId);

    Collection<Film> getPopularFilms(int count);

    boolean isContains(int id);
}
