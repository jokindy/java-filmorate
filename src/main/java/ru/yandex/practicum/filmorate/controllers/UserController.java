package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Slf4j
@RestController
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.info("Get users");
        return userService.getUsers();
    }

    @PostMapping("/users")
    public User add(@Valid @RequestBody User user) {
        log.info("Add new user");
        userService.addUser(user);
        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        log.info("Put user id: {}", user.getId());
        userService.putUser(user);
        return user;
    }

    @GetMapping("/users/{userId}")
    public User getUser(@PathVariable int userId) {
        log.info("Get user by id: {}", userId);
        return userService.getUser(userId);
    }

    @DeleteMapping("/users/{userId}")
    public String deleteUser(@PathVariable @Positive(message = "User id must be positive") int userId) {
        log.info("Delete user by id: {}", userId);
        return userService.deleteUser(userId);
    }

    @PutMapping("/users/{userId}/friends/{friendId}")
    public String addUserToFriends(@PathVariable int userId,
                                   @PathVariable int friendId) {
        log.info(String.format("Add friend id: %s to user id: %s", friendId, userId));
        return userService.addToFriends(userId, friendId);
    }

    @PatchMapping("/users/{userId}/friends/{friendId}")
    public String confirmFriendship(@PathVariable int userId,
                                    @PathVariable int friendId) {
        log.info(String.format("Confirm friendship between user id: %s and user id: %s", friendId, userId));
        return userService.confirmFriendship(userId, friendId);
    }

    @DeleteMapping("/users/{userId}/friends/{friendId}")
    public String deleteUserFromFriends(@PathVariable @Positive(message = "User id must be positive") int userId,
                                        @PathVariable @Positive(message = "Friend id must be positive") int friendId) {
        log.info(String.format("Delete friend id: %s  from user id: %s", friendId, userId));
        return userService.deleteFromFriends(userId, friendId);
    }

    @GetMapping("/users/{userId}/friends")
    public Collection<User> getUserFriends(@PathVariable @Positive(message = "User id must be positive") int userId) {
        log.info("Get user friends by id: {}", userId);
        return userService.getUserFriends(userId);
    }

    @GetMapping("/users/{userId}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable @Positive(message = "User id must be positive") int userId,
                                             @PathVariable @Positive(message = "User id must be positive") int otherId) {
        log.info(String.format("Get common friends between users id: %s and id: %s", userId, otherId));
        return userService.getCommonFriends(userId, otherId);
    }
}