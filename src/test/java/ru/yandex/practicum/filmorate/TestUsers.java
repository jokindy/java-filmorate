package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class TestUsers {

    public static final User commonUser1 = new User("mail@mail.ru", "login", "John",
            LocalDate.of(1995, 10, 3));
    public static final User updatedUser1 = new User("school@mail.ru", "login", "John",
            LocalDate.of(1995, 10, 3));
    public static final User commonUser2 = new User("gachi@mail.su", "Valeron", "Valera",
            LocalDate.of(1990, 8, 7));
    public static final User commonUser3 = new User("pie@mail.com", "Sherminator", "Eve",
            LocalDate.of(1985, 6, 15));
}