package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.film.DirectorDbStorage;

import java.util.Collection;

@Service
public class DirectorService {

    private final DirectorDbStorage storage;

    public DirectorService(DirectorDbStorage storage) {
        this.storage = storage;
    }

    public Collection<Director> getDirectors() {
        return storage.getDirectors();
    }

    public void addDirector(Director director) {
        storage.add(director);
    }

    public void putDirector(Director director) {
        storage.put(director);
    }

    public Director getDirector(int id) {
        return storage.getDirectorById(id);
    }

    public String deleteDirector(int id) {
        storage.deleteDirectorById(id);
        return "Director id: " + id + " deleted";
    }
}
