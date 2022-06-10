package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;

@Service
public class DirectorService {

    private final FilmStorage storage;

    public DirectorService(FilmStorage storage) {
        this.storage = storage;
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
}
