package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;

public class TestFilms {

    public static final Film commonFilm1 = new Film(1,"Batman", "Man without fear",
            LocalDate.of(2022, 3, 11), 135, 3, new MPA(1),
            new Director(1, "Michael Bay"));
    public static final Film updatedFilm1 = new Film(1,"Batman-3", "Man without fear",
            LocalDate.of(2022, 3, 11), 135, 3, new MPA(1),
            new Director(1, "Michael Bay"));
    public static final Film commonFilm2 = new Film(2,"Titanic", "Big ship goes to bottom",
            LocalDate.of(1997, 9, 17), 180, 10, new MPA(1),
            new Director(1, "Michael Bay"));
    public static final Film commonFilm3 = new Film(3, "Brother", "EVIL RUSSIAN",
            LocalDate.of(1998, 10, 15), 130, 7, new MPA(1),
            new Director(1, "Michael Bay"));
}