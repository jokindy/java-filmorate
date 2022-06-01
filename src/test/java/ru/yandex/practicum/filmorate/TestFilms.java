package ru.yandex.practicum.filmorate;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;

public class TestFilms {

    public static final Film commonFilm1 = new Film("Batman", "Man without fear",
            LocalDate.of(2022, 3, 11), 135, 3, new MPA(1));
    public static final Film updatedFilm1 = new Film("Batman-3", "Man without fear",
            LocalDate.of(2022, 3, 11), 135, 3, new MPA(1));
    public static final Film commonFilm2 = new Film("Titanic", "Big ship goes to bottom",
            LocalDate.of(1997, 9, 17), 180, 10, new MPA(1));
    public static final Film commonFilm3 = new Film("Brother", "EVIL RUSSIAN",
            LocalDate.of(1998, 10, 15), 130, 7, new MPA(1));
}