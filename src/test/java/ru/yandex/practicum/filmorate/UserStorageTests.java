package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.TestUsers.*;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserStorageTests {

    private final UserDbStorage userStorage;

    @Order(1)
    @Test
    public void testAddCommonUser() {
        userStorage.add(commonUser1);
        User testUser = userStorage.getUserById(1);
        Assertions.assertEquals(commonUser1, testUser);
    }

    @Order(2)
    @Test
    public void testAddCommonUserTwice() {
        Assertions.assertThrows(ModelAlreadyExistException.class, () -> userStorage.add(commonUser1));
    }

    @Order(3)
    @Test
    public void testPutSameUser() {
        Assertions.assertThrows(ModelAlreadyExistException.class, () -> userStorage.put(commonUser1));
    }

    @Order(4)
    @Test
    public void testPutUpdatedUser() {
        updatedUser1.setId(1);
        userStorage.put(updatedUser1);
        User testUser = userStorage.getUserById(1);
        Assertions.assertEquals(updatedUser1, testUser);
    }

    @Order(5)
    @Test
    public void testPutFriendInvitation() {
        userStorage.add(commonUser2);
        userStorage.putFriendInvitation(1, 2);
        Assertions.assertEquals(List.of(), userStorage.getUserFriends(1));
    }


    @Order(6)
    @Test
    public void testConfirmFriendInvitation() {
        userStorage.confirmFriendship(2, 1);
        Assertions.assertEquals(List.of(updatedUser1), userStorage.getUserFriends(2));
    }

    @Order(7)
    @Test
    public void testGetCommonFriends() {
        userStorage.add(commonUser3);
        userStorage.putFriendInvitation(1, 3);
        userStorage.confirmFriendship(1, 3);
        Assertions.assertEquals(List.of(updatedUser1), userStorage.getCommonFriends(2, 3));
    }

    @Order(8)
    @Test
    public void testGetAllUsers() {
        Collection<User> list = List.of(updatedUser1, commonUser2, commonUser3);
        Assertions.assertEquals(list, userStorage.getUsers());
    }

    @Order(9)
    @Test
    public void testDeleteUser() {
        userStorage.deleteUserById(1);
        Assertions.assertThrows(ModelNotFoundException.class, () -> userStorage.getUserById(1));
    }
}