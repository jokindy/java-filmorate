package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    void add(User user);

    void put(User user);

    User getUserById(int id);

    void deleteUserById(int id);

    Collection<User> getUserFriends(int id);

    void putFriendInvitation(int id, int friendId);

    void confirmFriendship(int id, int friendId);

    void deleteFriends(int id, int friendId);

    Collection<User> getCommonFriends(int id1, int id2);

    Collection<User> getUsers();

    Collection<Event> getUserEvents(int userId);

    boolean isContains(int id);
}
