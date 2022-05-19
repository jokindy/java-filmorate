package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> map;
    private int id = 0;

    public InMemoryUserStorage() {
        this.map = new HashMap<>();
    }

    @Override
    public void add(User user) {
        if (map.containsValue(user)) {
            throw new ModelAlreadyExistException("This user is already added");
        }
        id++;
        user.setId(id);
        map.put(id, user);
    }

    @Override
    public void put(User user) {
        int userId = user.getId();
        if (map.containsKey(userId) && map.containsValue(user)) {
            throw new ModelAlreadyExistException("User id: " + userId + " is the same");
        } else if (map.containsKey(userId)) {
            map.replace(userId, user);
        } else {
            throw new ModelNotFoundException("User id: " + userId + " not found");
        }
    }

    @Override
    public User getUserById(int id) {
        return map.values().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ModelNotFoundException(String.format("User id: %s not found", id)));
    }

    @Override
    public void deleteUserById(int id) {
        if (map.containsKey(id)) {
            User user = getUserById(id);
            Set<Integer> userFriendId = new HashSet<>(user.getFriendsId());
            for (Integer friendId : userFriendId) {
                deleteFriends(id, friendId);
            }
            map.remove(id);
        } else {
            throw new ModelNotFoundException(String.format("User id: %s not found", id));
        }
    }

    @Override
    public void addFriends(int id, int friendId) {
        getUserById(id).addUserToFriend(friendId);
        getUserById(friendId).addUserToFriend(id);
    }

    @Override
    public void deleteFriends(int id, int friendId) {
        getUserById(id).deleteUserFromFriend(friendId);
        getUserById(friendId).deleteUserFromFriend(id);
    }

    @Override
    public Collection<User> getUsers() {
        return map.values();
    }

    @Override
    public boolean isContains(int id) {
        return map.containsKey(id);
    }
}
