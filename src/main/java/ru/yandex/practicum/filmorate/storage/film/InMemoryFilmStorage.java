package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component
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
            add(film);
        }
    }

    @Override
    public Collection<Film> getFilms() {
        return map.values();
    }

    @Override
    public Film getFilmById(int id) {
        return map.values().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ModelNotFoundException(String.format("Film id: %s not found", id)));
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
