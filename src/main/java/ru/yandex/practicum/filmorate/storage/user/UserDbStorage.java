package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NoFriendsException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import static ru.yandex.practicum.filmorate.model.event.EventType.*;
import static ru.yandex.practicum.filmorate.model.event.Operation.*;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;
    private final ApplicationEventPublisher eventPublisher;

    public UserDbStorage(JdbcTemplate jdbc, ApplicationEventPublisher eventPublisher) {
        this.jdbc = jdbc;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void add(User user) {
        String email = user.getEmail();
        SqlRowSet userRows = jdbc.queryForRowSet("SELECT * FROM users WHERE email = ?", email);
        if (!userRows.isBeforeFirst()) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                    .withTableName("USERS")
                    .usingGeneratedKeyColumns("user_id");
            int id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
            user.setId(id);
        } else {
            throw new ModelAlreadyExistException("This user is already added");
        }
    }

    @Override
    public void put(User user) {
        int userId = user.getId();
        User anotherUser = getUserById(userId);
        if (user.equals(anotherUser)) {
            throw new ModelAlreadyExistException("User id: " + userId + " is the same");
        }
        jdbc.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), userId);
    }

    @Override
    public User getUserById(int id) {
        try {
            return jdbc.queryForObject("SELECT * FROM users WHERE user_id = ?", this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("User id: %s not found", id));
        }
    }

    @Override
    public void deleteUserById(int id) {
        getUserById(id);
        jdbc.update("DELETE FROM users WHERE user_id = ?", id);
    }

    @Override
    public Collection<User> getUserFriends(int id) {
        return jdbc.query("SELECT * FROM (select f1.USER2_ID AS friend_ID\n from FRIENDS as F1\n" +
                "WHERE f1.USER1_ID = ? AND STATUS = true UNION select f2.USER1_ID AS friend_ID\n" +
                "from FRIENDS AS f2 WHERE f2.USER2_ID = ? AND STATUS = true) AS F\n" +
                "LEFT JOIN USERS AS u on f.friend_ID = USER_ID", this::mapRowToUser, id, id);
    }

    @Override
    public void putFriendInvitation(int id, int friendId) {
        boolean isExist = isFriendsExists(id, friendId) || isFriendsExists(friendId, id);
        if (!isExist) {
            jdbc.update("INSERT INTO friends(user1_id, user2_id, status) VALUES (?, ?, ?)",
                    id, friendId, false);
            int friendshipId = getFriendshipId(id, friendId);
            Event event = Event.getEvent(id, FRIEND, ADD, friendshipId);
            eventPublisher.publishEvent(event);
        } else {
            throw new ModelAlreadyExistException(String.format("User id: %s already have invitation" +
                    " or friendship from user id: %s", friendId, id));
        }
    }

    @Override
    public void confirmFriendship(int id, int friendId) {
        String sql = String.format("UPDATE friends SET status = true %s", checkSide(id, friendId));
        int friendshipId = getFriendshipId(id, friendId);
        jdbc.update(sql, friendId, id);
        Event event = Event.getEvent(friendId, FRIEND, UPDATE, friendshipId);
        eventPublisher.publishEvent(event);
    }

    @Override
    public Collection<User> getCommonFriends(int id1, int id2) {
        Collection<User> user1Friends = getUserFriends(id1);
        Collection<User> user2Friends = getUserFriends(id2);
        user1Friends.retainAll(user2Friends);
        return user1Friends;
    }

    @Override
    public void deleteFriends(int id, int friendId) {
        String sql = String.format("DELETE FROM friends %s", checkSide(id, friendId));
        int friendshipId = getFriendshipId(id, friendId);
        jdbc.update(sql, id, friendId);
        Event event = Event.getEvent(id, FRIEND, REMOVE, friendshipId);
        eventPublisher.publishEvent(event);
    }

    @Override
    public Collection<User> getUsers() {
        return jdbc.query("SELECT * FROM users", this::mapRowToUser);
    }

    @Override
    public Collection<Event> getUserEvents(int userId) {
        return jdbc.query("SELECT * FROM events WHERE user_id = ?", this::mapRowToEvent, userId);
    }

    @Override
    public boolean isContains(int id) {
        SqlRowSet userRows = jdbc.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
        return userRows.next();
    }

    private User mapRowToUser(ResultSet userRows, int rowNum) throws SQLException {
        int id = userRows.getInt("user_id");
        String email = userRows.getString("email");
        String login = userRows.getString("login");
        String name = userRows.getString("name");
        LocalDate birthday = userRows.getDate("birthday").toLocalDate();
        User user = new User(email, login, name, birthday);
        user.setId(id);
        return user;
    }

    private Event mapRowToEvent(ResultSet eventRows, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(eventRows.getInt("event_id"))
                .timestamp(eventRows.getTimestamp("timestamp").toInstant())
                .userId(eventRows.getInt("user_id"))
                .eventType(EventType.valueOf(eventRows.getString("event_type")))
                .operation(Operation.valueOf(eventRows.getString("operation")))
                .entityId(eventRows.getInt("entity_id")).build();
    }


    private boolean isFriendsExists(int id1, int id2) {
        String sql = "SELECT status FROM friends WHERE user1_id = ? AND user2_id = ?";
        try {
            jdbc.queryForObject(sql, Boolean.class, id1, id2);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private Integer getFriendshipId(int id1, int id2) {
        try {
            return jdbc.queryForObject("SELECT friend_id FROM friends WHERE user1_id = ? AND user2_id = ?",
                    Integer.class, id1, id2);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    private String checkSide(int id1, int id2) {
        boolean isExistLeft = isFriendsExists(id1, id2);
        boolean isExistRight = isFriendsExists(id2, id1);
        if (isExistLeft) {
            return "WHERE user1_id = ? AND user2_id = ?";
        } else if (isExistRight) {
            return "WHERE user2_id = ? AND user1_id = ?";
        } else {
            throw new NoFriendsException(String.format("User id: %s don't have invitation" +
                    " or friendship with user id: %s", id2, id1));
        }
    }

    @EventListener
    public void handleEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbc)
                .withTableName("EVENTS")
                .usingGeneratedKeyColumns("event_id");
        simpleJdbcInsert.execute(event.toMap());
    }
}