package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> map;
    private int id = 0;

    public InMemoryFilmStorage() {
        this.map = new HashMap<>();
    }

    @Override
    public void add(Film film) {
        if (map.containsValue(film)) {
            throw new ModelAlreadyExistException("This film is already added");
        }
        id++;
        film.setId(id);
        map.put(id, film);
    }

    @Override
    public void put(Film film) {
        int filmId = film.getId();
        if (map.containsKey(filmId) && map.containsValue(film)) {
            throw new ModelAlreadyExistException("Film id: " + filmId + " is the same");
        } else if (map.containsKey(filmId)) {
            map.replace(filmId, film);
        } else {
            throw new ModelNotFoundException("Film id: " + filmId + " not found");
        }
    }

    @Override
    public Collection<Film> getFilms() {
        return map.values();
    }

    @Override
    public Collection<MPA> getAllMpa() {
        //реализовано в БД
        return null;
    }

    @Override
    public Collection<Genre> getAllGenres() {
        //реализовано в БД
        return null;
    }

    @Override
    public Film getFilmById(int id) {
        return map.values().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ModelNotFoundException(String.format("Film id: %s not found", id)));
    }

    @Override
    public MPA getMpaById(int id) {
        //реализовано в БД
        return null;
    }

    @Override
    public Genre getGenreById(int id) {
        //реализовано в БД
        return null;
    }

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
        //реализовано в БД
        return null;
    }

    @Override
    public void deleteFilmById(int id) {
        if (map.containsKey(id)) {
            map.remove(id);
        } else {
            throw new ModelNotFoundException(String.format("Film id: %s not found", id));
        }
    }

    @Override
    public void putLike(int id, int userId) {
        getFilmById(id).putUserLike(userId);
    }

    @Override
    public void deleteLike(int id, int userId) {
        getFilmById(id).deleteUserLike(userId);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        return getFilms().stream()
                .sorted(Comparator.comparingInt(Film::getUserLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isContains(int id) {
        return map.containsKey(id);
    }
}