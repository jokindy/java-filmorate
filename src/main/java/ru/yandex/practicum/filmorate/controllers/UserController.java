package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.utilities.Handler;
import ru.yandex.practicum.filmorate.utilities.Storage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Slf4j
@RestController
public class UserController {

    private final Storage<User> users = new Storage<>("User");

    @GetMapping("/users")
    public Map<Integer, User> findAll() {
        log.info("Get users");
        return users.getMap();
    }

    @PostMapping("/users")
    public String add(@Valid @RequestBody User user) {
        return new Handler<User>().handle(POST, users, user);
    }

    @PutMapping("/users")
    public String update(@Valid @RequestBody User user) {
        return new Handler<User>().handle(PUT, users, user);
    }
}
