package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    void add(User user);

    void put(User user);

    User getUserById(int id);

    void deleteUserById(int id);

    void addFriends(int id, int friendId);

    void deleteFriends(int id, int friendId);

    Collection<User> getUsers();

    boolean isContains(int id);

}
