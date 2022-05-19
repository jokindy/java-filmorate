package ru.yandex.practicum.filmorate.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NoFriendsException;
import ru.yandex.practicum.filmorate.exceptions.SameIdException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final InMemoryUserStorage storage;
    private final InMemoryFilmStorage filmStorage;

    public UserService(InMemoryUserStorage storage, InMemoryFilmStorage filmStorage) {
        this.storage = storage;
        this.filmStorage = filmStorage;
    }

    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public void addUser(User user) {
        storage.add(user);
    }

    public void putUser(User user) {
         storage.put(user);
    }

    public User getUser(int id) {
        return storage.getUserById(id);
    }

    public String deleteUser(int userId) {
        User user = storage.getUserById(userId);
        Set<Integer> likedFilmsId = new HashSet<>(user.getLikedFilmsId());
        for (Integer filmId : likedFilmsId) {
            filmStorage.deleteLike(filmId, userId);
        }
        storage.deleteUserById(userId);
        return "User id: " + userId + " deleted";
    }

    public String addToFriends(int userId, int friendId) {
        checkIds(userId, friendId);
        storage.addFriends(userId, friendId);
        return String.format("User id: %s added as friend to user id: %s", friendId, userId);
    }

    public String deleteFromFriends(int userId, int friendId) {
        checkIds(userId, friendId);
        storage.deleteFriends(userId, friendId);
        return String.format("User id: %s removed as friend from user id: %s", friendId, userId);
    }

    public Collection<User> getUserFriends(int id) {
        if (!storage.isContains(id)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", id));
        }
        Set<Integer> userFriends = storage.getUserById(id).getFriendsId();
        if (userFriends.isEmpty()) {
            throw new NoFriendsException("User hasn't friends :(");
        }
        return userFriends.stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(int id1, int id2) {
        checkIds(id1, id2);
        Set<Integer> ids1 = new HashSet<>(storage.getUserById(id1).getFriendsId());
        Set<Integer> ids2 = storage.getUserById(id2).getFriendsId();
        ids1.retainAll(ids2);
        return ids1.stream()
                .map(storage::getUserById)
                .collect(Collectors.toList());
    }

    private void checkIds(int userId, int friendId) {
        if (userId == friendId) {
            throw new SameIdException("Same id");
        }
        if (!storage.isContains(userId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", userId));
        }
        if (!storage.isContains(friendId)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", friendId));
        }
    }
}