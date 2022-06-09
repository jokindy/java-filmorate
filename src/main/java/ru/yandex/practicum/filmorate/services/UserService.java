package ru.yandex.practicum.filmorate.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.SameIdException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Service
public class UserService {

    private final UserStorage storage;

    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
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
        storage.deleteUserById(userId);
        return "User id: " + userId + " deleted";
    }

    public String addToFriends(int userId, int friendId) {
        checkIds(userId, friendId);
        storage.putFriendInvitation(userId, friendId);
        return String.format("User id: %s send invitation of friendship to user id: %s", userId, friendId);
    }

    public String confirmFriendship(int userId, int friendId) {
        checkIds(userId, friendId);
        storage.confirmFriendship(userId, friendId);
        return String.format("User id: %s added user id: %s to friends", friendId, userId);
    }

    public String deleteFromFriends(int userId, int friendId) {
        checkIds(userId, friendId);
        storage.deleteFriends(userId, friendId);
        return String.format("User id: %s removed as friend from user id: %s", friendId, userId);
    }

    public Collection<User> getUserFriends(int id) {
        return storage.getUserFriends(id);
    }

    public Collection<User> getCommonFriends(int id1, int id2) {
        checkIds(id1, id2);
        return storage.getCommonFriends(id1, id2);
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