package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ModelAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.ModelNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.NoFriendsException;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operations;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(User user) {
        String email = user.getEmail();
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE email = ?", email);
        if (!userRows.isBeforeFirst()) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
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
        jdbcTemplate.update("UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), userId);
    }

    @Override
    public User getUserById(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM users WHERE user_id = ?", this::mapRowToUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ModelNotFoundException(String.format("User id: %s not found", id));
        }
    }

    @Override
    public void deleteUserById(int id) {
        getUserById(id);
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", id);
    }

    @Override
    public Collection<User> getUserFriends(int id) {
        List<User> friends = new ArrayList<>();
        friends.addAll(jdbcTemplate.query("SELECT * FROM friends AS f\n LEFT JOIN users AS u ON f.user2_id = u.user_id\n"
                + "WHERE user1_id = ?\n;", this::mapRowToUser, id));
        friends.addAll(jdbcTemplate.query("SELECT * FROM friends AS f\n LEFT JOIN users AS u ON f.user1_id = u.user_id\n"
                + "WHERE user2_id = ? AND status = true\n;", this::mapRowToUser, id));
        return friends;
    }

    @Override
    public void putFriendInvitation(int id, int friendId) {
        boolean isExist = isFriendsExists(id, friendId) || isFriendsExists(friendId, id);
        if (!isExist) {
            jdbcTemplate.update("INSERT INTO friends(user1_id, user2_id, status) VALUES (?, ?, ?)",
                    id, friendId, false);
            jdbcTemplate.update(
                    "INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                            "VALUES (now(), ?, 'FRIEND', 'ADD', (select FRIEND_ID from FRIENDS" +
                            " where USER1_ID = ? and USER2_ID = ?))", id, id, friendId);
        } else {
            throw new ModelAlreadyExistException(String.format("User id: %s already have invitation" +
                    " or friendship from user id: %s", friendId, id));
        }
    }

    @Override
    public void confirmFriendship(int id, int friendId) {
        boolean isExistLeft = isFriendsExists(id, friendId);
        boolean isExistRight = isFriendsExists(friendId, id);
        String sql = "UPDATE friends SET status = true WHERE user1_id = ? and user2_id = ?";
        if (isExistLeft) {
            jdbcTemplate.update(sql, id, friendId);
            addFriendshipUpdateToEvents(id, friendId);
        } else if (isExistRight) {
            jdbcTemplate.update(sql, friendId, id);
            addFriendshipUpdateToEvents(friendId, id);
        } else {
            throw new NoFriendsException(String.format("User id: %s don't have invitation" +
                    " or friendship with user id: %s", friendId, id));
        }
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
        boolean isExistLeft = isFriendsExists(id, friendId);
        boolean isExistRight = isFriendsExists(friendId, id);
        if (isExistLeft) {
            jdbcTemplate.update(
                    "INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                            "VALUES (now(), ?, 'FRIEND', 'REMOVE'," +
                            " (select FRIEND_ID from FRIENDS where USER1_ID = ? and USER2_ID = ?))", id, id, friendId);
            jdbcTemplate.update("DELETE FROM friends WHERE user1_id = ? and user2_id = ?", id, friendId);
        } else if (isExistRight) {
            jdbcTemplate.update(
                    "INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                            "VALUES (now(), ?, 'FRIEND', 'REMOVE', " +
                            "(select FRIEND_ID from FRIENDS where USER1_ID = ? and USER2_ID = ? ))", friendId, friendId, id);
            jdbcTemplate.update("DELETE FROM friends WHERE user1_id = ? and user2_id = ?", friendId, id);
        } else {
            throw new NoFriendsException(String.format("User id: %s don't have invitation" +
                    " or friendship with user id: %s", friendId, id));
        }
    }

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query("SELECT * FROM users", this::mapRowToUser);
    }

    @Override
    public Collection<Event> getUserEvents(Integer id) {
        String sql = "select * from EVENTS where USER_ID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), id);
    }

    @Override
    public boolean isContains(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", id);
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

    private Event makeEvent(ResultSet rs) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .timestamp(rs.getTimestamp("timestamp").toInstant())
                .userId(rs.getInt("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operations.valueOf(rs.getString("operation")))
                .entityId(rs.getInt("entity_id"))
                .build();
    }

    private void addFriendshipUpdateToEvents(int id1, int id2) {
        Integer friendshipId = jdbcTemplate.queryForObject(
                "SELECT FRIEND_ID FROM FRIENDS WHERE USER1_ID = ? and USER2_ID = ?", Integer.class, id1, id2
        );
        String eventRequestSql = "SELECT * FROM EVENTS " +
                "WHERE USER_ID=? and EVENT_TYPE = 'FRIEND' and OPERATION = 'UPDATE' and ENTITY_ID = ?";
        try {
            jdbcTemplate.queryForObject(eventRequestSql, (rs, rowNum) -> makeEvent(rs), id1, friendshipId);
            throw new ModelAlreadyExistException("Friendship already confirmed!");
        } catch (DataAccessException e) {
            String sql = "INSERT INTO EVENTS(TIMESTAMP, USER_ID, EVENT_TYPE, OPERATION, ENTITY_ID) " +
                    "VALUES (now(), ?, 'FRIEND', 'UPDATE', ?)";
            jdbcTemplate.update(sql, id1, friendshipId);
        }
    }

    private String getFriendsQuery(int id) {
        String sql = "SELECT * FROM friends AS f\n LEFT JOIN users AS u ON f.user2_id = u.user_id\n" +
                "WHERE user1_id = ?\n;";
        List<String> strLst = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString(1), id);
        if (strLst.size() == 0) {
            sql = "SELECT * FROM friends AS f\n LEFT JOIN users AS u ON f.user1_id = u.user_id\n" +
                    "WHERE user2_id = ? AND status = true\n;";
        }
        return sql;
    }

    private boolean isFriendsExists(int id1, int id2) {
        String sql = "SELECT status FROM friends WHERE user1_id = ? AND user2_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, Boolean.class, id1, id2);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}