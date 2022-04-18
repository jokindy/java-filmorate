package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class TestUsers {

    public static final User user = new User("mail@mail.ru", "login", "John",
            LocalDate.of(1995, 10, 3));
    public static final User updatedUser = new User("school@mail.ru", "login", "John",
            LocalDate.of(1995, 10, 3));
    public static final User userWithEmptyEmail = new User("", "login", "John",
            LocalDate.of(1995, 10, 3));
    public static final User userWithEmptyLogin = new User("mail@mail.ru", "", "John",
            LocalDate.of(1995, 10, 3));
    public static final User userWithLoginWithSpaces = new User("mail@mail.ru", "lo gin", "John",
            LocalDate.of(1995, 10, 3));
    public static final User userWithEmptyName = new User("mail@mail.ru", "login", "",
            LocalDate.of(1995, 10, 3));
    public static final User userFromFuture = new User("mail@mail.ru", "login", "",
            LocalDate.of(2095, 10, 3));
}
