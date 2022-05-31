package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NoFriendsException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> map;
    private final List<Friendship> friendsList;
    private int id = 0;

    public InMemoryUserStorage() {
        this.map = new HashMap<>();
        friendsList = new ArrayList<>();
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
    public Collection<User> getUserFriends(int id) {
        if (!map.containsKey(id)) {
            throw new ModelNotFoundException(String.format("User id: %s not found", id));
        }
        Set<Integer> userFriends = map.get(id).getFriendsId();
        if (userFriends.isEmpty()) {
            throw new NoFriendsException("User hasn't friends :(");
        }
        return userFriends.stream()
                .map(map::get)
                .collect(Collectors.toList());
    }

    @Override
    public void putFriendInvitation(int id, int friendId) {
        Friendship friendship = new Friendship(id, friendId);
        if (!friendsList.contains(friendship)) {
            friendsList.add(friendship);
        } else {
            throw new ModelAlreadyExistException(String.format("User id: %s already have invitation" +
                    " or friendship from user id: %s", friendId, id));
        }
    }

    @Override
    public void confirmFriendship(int id, int friendId) {
        Friendship friendship = new Friendship(id, friendId);
        if (friendsList.contains(friendship)) {
            int index = friendsList.indexOf(friendship);
            friendship.setStatus(true);
            friendsList.set(index, friendship);
            getUserById(id).addUserToFriend(friendId);
            getUserById(friendId).addUserToFriend(id);
        } else {
            throw new NoFriendsException(String.format("User id: %s don't have invitation" +
                    " or friendship with user id: %s", friendId, id));
        }
    }

    @Override
    public Collection<User> getCommonFriends(int id1, int id2) {
        Set<Integer> ids1 = new HashSet<>(getUserById(id1).getFriendsId());
        Set<Integer> ids2 = getUserById(id2).getFriendsId();
        ids1.retainAll(ids2);
        return ids1.stream()
                .map(map::get)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFriends(int id, int friendId) {
        Friendship friendship = new Friendship(id, friendId);
        friendship.setStatus(true);
        if (friendsList.contains(friendship)) {
            friendsList.remove(friendship);
            getUserById(id).deleteUserFromFriend(friendId);
            getUserById(friendId).deleteUserFromFriend(id);
        } else {
            throw new NoFriendsException(String.format("User id: %s don't have invitation" +
                    " or friendship with user id: %s", friendId, id));
        }
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