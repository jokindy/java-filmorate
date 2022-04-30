package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.utilities.Storage;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Map;

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
        String s = users.add(user);
        log.info(s);
        return s;
    }

    @PutMapping("/users")
    public String update(@Valid @RequestBody User user) {
        String s = users.put(user);
        log.info(s);
        return s;
    }
}
